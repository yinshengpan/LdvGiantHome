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
import com.ledvance.usecase.device.DeleteDeviceUseCase
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetDeviceListStateUseCase
import com.ledvance.usecase.device.GetDevicesUseCase
import com.ledvance.usecase.device.SyncDeviceInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
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
            device.copy(isOnline = onlineMap[device.deviceId] ?: false)
        }
        HomeContract.UiState.Success(devices = mergedDevices, loading = state.loading)
    }.onStart {
        Timber.d("Loading home page")
    }.onEach {
        Timber.d("Home page is loaded")
    }.catch { error ->
        Timber.e(error, "Failed to load home page")
        emit(HomeContract.UiState.Success(devices = listOf()))
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(),
        initialValue = HomeContract.UiState.Loading
    )

    init {
        syncDeviceInfoUseCase(viewModelScope)
        startAutoReconnect()
    }

    private fun startAutoReconnect() {
        viewModelScope.launch {
            while (true) {
                delay(15_000)
                val currentState = uiState.value
                if (currentState is HomeContract.UiState.Success) {
                    val connectedCount = currentState.devices.count { it.isOnline }
                    if (connectedCount < 3) {
                        val devicesToConnect = currentState.devices
                            .filter { !it.isOnline }
                            .take(3 - connectedCount)

                        if (devicesToConnect.isNotEmpty()) {
                            Timber.tag(TAG).d("Auto-reconnecting ${devicesToConnect.size} devices")
                            connectDevices(devicesToConnect)
                        }
                    }
                }
            }
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