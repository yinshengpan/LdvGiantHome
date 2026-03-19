package com.ledvance.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.ble.core.ConnectionManager
import com.ledvance.ble.usecase.DeviceSwitchUseCase
import com.ledvance.database.usecase.GetDevicesUseCase
import com.ledvance.domain.bean.DeviceUiItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : HomeViewModel
 */
import kotlinx.coroutines.flow.combine
import com.ledvance.ble.core.DeviceRegistry

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val getDevicesUseCase: GetDevicesUseCase,
    private val deviceSwitchUseCase: DeviceSwitchUseCase,
    private val connectionManager: ConnectionManager,
    private val deviceRegistry: DeviceRegistry,
) : ViewModel(), HomeContract {

    override val uiState: StateFlow<HomeContract.UiState> = combine(
        getDevicesUseCase(),
        deviceRegistry.devicesFlow
    ) { dbDevices, bleDevices ->
        if (dbDevices.isEmpty()) {
            HomeContract.UiState.Empty
        } else {
            val onlineMap = bleDevices.associate { it.mac to it.isOnline }
            val connectedMap = bleDevices.associate { it.mac to it.isConnected }
            Timber.d("onlineMap:$onlineMap,connectedMap:$connectedMap")
            HomeContract.UiState.Success(
                devices = dbDevices,
                onlineMap = onlineMap,
                connectedMap = connectedMap
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

    override fun onSwitchChange(device: DeviceUiItem, switch: Boolean) {
        viewModelScope.launch {
            deviceSwitchUseCase(
                DeviceSwitchUseCase.Param(
                    address = device.address,
                    switch = switch
                )
            )
        }
    }

    override fun connectDevice(mac: String) {
        connectionManager.requestConnect(mac)
    }

    override fun disconnectDevice(mac: String) {
        connectionManager.disconnect(mac)
    }

    override fun connectDevices(devices: List<DeviceUiItem>) {
        devices.forEach {
            connectionManager.requestConnect(it.address)
        }
    }
}