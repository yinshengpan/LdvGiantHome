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

        @Immutable
        data object Loading : UiState

        @Immutable
        data class Success(
            val devices: List<DeviceUiItem>,
        ) : UiState

        @Immutable
        data object Empty : UiState
    }

    val uiState: StateFlow<UiState>

    fun onSwitchChange(deviceId: DeviceId, switch: Boolean)
    fun connectDevice(deviceId: DeviceId)
    fun disconnectDevice(deviceId: DeviceId)
    fun connectDevices(devices: List<DeviceUiItem>)
    fun disconnectAllDevices()
    fun onDeleteDevice(deviceId: DeviceId)
}