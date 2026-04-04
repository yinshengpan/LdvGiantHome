package com.ledvance.home

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : HomeViewModel
 */

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.ble.core.ConnectionManager
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceUiItem
import com.ledvance.ui.component.SnackbarManager
import com.ledvance.usecase.device.AutoConnectUseCase
import com.ledvance.usecase.device.DeleteDeviceUseCase
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetDeviceListStateUseCase
import com.ledvance.usecase.device.GetDevicesUseCase
import com.ledvance.usecase.device.SyncDeviceInfoUseCase
import com.ledvance.utils.extensions.tryCatchReturn
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val getDevicesUseCase: GetDevicesUseCase,
    private val deviceControlUseCase: DeviceControlUseCase,
    private val connectionManager: ConnectionManager,
    private val getDeviceListStateUseCase: GetDeviceListStateUseCase,
    private val syncDeviceInfoUseCase: SyncDeviceInfoUseCase,
    private val deleteDeviceUseCase: DeleteDeviceUseCase,
    private val autoConnectUseCase: AutoConnectUseCase,
) : ViewModel(), HomeContract {

    private val TAG = "HomeViewModel"
    private val screenState = MutableStateFlow(ScreenState())
    override val uiState: StateFlow<HomeContract.UiState> = combine(
        getDevicesUseCase(),
        getDeviceListStateUseCase(),
        screenState
    ) { dbDevices, deviceStateList, state ->
        val onlineMap = deviceStateList.associate { it.deviceId to it.isOnline }
        Timber.tag(TAG).d("onlineMap:$onlineMap")
        val mergedDevices = dbDevices.map { device ->
            val isOnline = onlineMap[device.deviceId] ?: false
            Timber.tag(TAG).d("merge -> deviceId=${device.deviceId}, dbOnline=${device.isOnline}, newOnline=$isOnline")
            device.copy(isOnline = isOnline)
        }
        val onlineDeviceCount = mergedDevices.filter { it.isOnline }.size
        HomeContract.UiState.Success(devices = mergedDevices, loading = state.loading, onlineDeviceCount = onlineDeviceCount)
    }.onStart {
        Timber.tag(TAG).d("Home -> start loading")
    }.onEach { uiState ->
        Timber.tag(TAG).d("Home -> state updated: $uiState")
    }.catch { error ->
        Timber.tag(TAG).e(error, "Home -> failed to load")
        emit(HomeContract.UiState.Success(devices = listOf()))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HomeContract.UiState.Loading
    )

    /** 追踪 AutoConnectUseCase 运行的 Job，用于在页面不可见时取消扫描 */
    private var autoConnectJob: Job? = null

    init {
        syncDeviceInfoUseCase(viewModelScope)
    }

    override fun setPageVisibility(visible: Boolean) {
        Timber.tag(TAG).d("setPageVisibility: visible=$visible")
        if (visible) {
            // 页面进入前台：若扫描任务未运行则启动
            if (autoConnectJob?.isActive != true) {
                autoConnectJob = tryCatchReturn { autoConnectUseCase(viewModelScope) }
                Timber.tag(TAG).d("AutoConnect started")
            }
        } else {
            // 页面退到后台：取消扫描任务，停止 BLE 扫描
            autoConnectJob?.cancel()
            autoConnectJob = null
            Timber.tag(TAG).d("AutoConnect stopped")
        }
    }

    override fun onSwitchChange(deviceId: DeviceId, switch: Boolean) {
        Timber.tag(TAG).d("onSwitchChange: deviceId=%s, switch=%s", deviceId, switch)
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            val success = deviceControlUseCase.setPower(deviceId, switch)
            if (!success) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(loading = false) }
        }
    }

    override fun asyncConnectDevice(deviceId: DeviceId) {
        Timber.tag(TAG).d("asyncConnectDevice: deviceId=%s", deviceId)
        connectionManager.requestConnect(deviceId)
    }

    override fun connectDevice(deviceId: DeviceId) {
        Timber.tag(TAG).d("connectDevice: deviceId=%s", deviceId)
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            val success = deviceControlUseCase.connectDevice(deviceId)
            if (!success) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(loading = false) }
        }
    }

    override fun disconnectDevice(deviceId: DeviceId) {
        Timber.tag(TAG).d("disconnectDevice: deviceId=%s", deviceId)
        connectionManager.disconnect(deviceId)
    }

    override fun connectDevices(devices: List<DeviceUiItem>) {
        devices.forEach {
            connectionManager.requestConnect(it.deviceId)
        }
    }

    override fun disconnectAllDevices() {
        Timber.tag(TAG).d("disconnectAllDevices")
        connectionManager.disconnectAll()
    }

    override fun onDeleteDevice(deviceId: DeviceId) {
        Timber.tag(TAG).d("onDeleteDevice: deviceId=%s", deviceId)
        viewModelScope.launch {
            screenState.update { it.copy(loading = true) }
            val result = runCatching { deleteDeviceUseCase(deviceId) }
            if (result.isFailure) {
                SnackbarManager.showGenericError()
            }
            screenState.update { it.copy(loading = false) }
        }
    }

    private data class ScreenState(
        val loading: Boolean = false,
    )
}