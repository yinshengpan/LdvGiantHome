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
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetDeviceListStateUseCase
import com.ledvance.usecase.device.GetDevicesUseCase
import com.ledvance.usecase.device.SyncDeviceInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
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
) : ViewModel(), HomeContract {
    private val TAG = "HomeViewModel"
    override val uiState: StateFlow<HomeContract.UiState> = combine(
        getDevicesUseCase(),
        getDeviceListStateUseCase()
    ) { dbDevices, deviceStateList ->
        if (dbDevices.isEmpty()) {
            HomeContract.UiState.Empty
        } else {
            val onlineMap = deviceStateList.associate { it.deviceId to it.isOnline }
            Timber.tag(TAG).d("onlineMap:$onlineMap")
            HomeContract.UiState.Success(
                devices = dbDevices,
                onlineMap = onlineMap,
            )
        }
    }
        .onStart {
            Timber.d("Loading home page")
        }
        .onEach {
            Timber.d("Home page id loaded")
        }
        .catch { error ->
            Timber.e(error, "Failed to load home page")
            emit(HomeContract.UiState.Empty)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = HomeContract.UiState.Loading
        )

    init {
        syncDeviceInfoUseCase(viewModelScope)
    }

    override fun onSwitchChange(deviceId: DeviceId, switch: Boolean) {
        viewModelScope.launch {
            deviceControlUseCase.setPower(deviceId, switch)
        }
    }

    override fun connectDevice(deviceId: DeviceId) {
        connectionManager.requestConnect(deviceId)
    }

    override fun disconnectDevice(deviceId: DeviceId) {
        connectionManager.disconnect(deviceId)
    }

    override fun connectDevices(devices: List<DeviceUiItem>) {
        devices.forEach {
            connectionManager.requestConnect(it.deviceId)
        }
    }
}