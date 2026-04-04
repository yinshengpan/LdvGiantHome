package com.ledvance.home

import androidx.compose.runtime.Immutable
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceUiItem
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:36
 * Describe : HomeContract
 */
internal interface HomeContract {

    @Immutable
    sealed interface UiState {
        data object Loading : UiState

        @Immutable
        data class Success(
            val appName: String = "",
            val devices: List<DeviceUiItem> = listOf(),
            val onlineDeviceCount: Int = 0,
            val loading: Boolean = false,
        ) : UiState
    }

    val uiState: StateFlow<UiState>

    fun onSwitchChange(deviceId: DeviceId, switch: Boolean)
    fun asyncConnectDevice(deviceId: DeviceId)
    fun connectDevice(deviceId: DeviceId)
    fun disconnectDevice(deviceId: DeviceId)
    fun connectDevices(devices: List<DeviceUiItem>)
    fun disconnectAllDevices()
    fun onDeleteDevice(deviceId: DeviceId)
    fun setPageVisibility(visible: Boolean)
}