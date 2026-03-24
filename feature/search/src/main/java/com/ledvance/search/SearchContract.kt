package com.ledvance.search

import androidx.compose.runtime.Immutable
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.domain.bean.DeviceId
import com.ledvance.ui.utils.OneTimeAction
import com.ledvance.ui.utils.OneTimeActionConsumerContract
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:36
 * Describe : SearchContract
 */
internal interface SearchContract :
    OneTimeActionConsumerContract<SearchContract.SearchOneTimeAction> {

    sealed interface SearchOneTimeAction : OneTimeAction {
        data class AddDeviceSuccess(val deviceId: DeviceId) : SearchOneTimeAction
    }

    @Immutable
    sealed interface UiState {

        @Immutable
        data class Success(
            val loading: Boolean = false,
            val devices: List<ScannedDevice> = listOf(),
        ) : UiState
    }

    val uiState: StateFlow<UiState.Success>

    fun startBleScan()

    fun stopBleScan()

    fun addDevice(scannedDevice: ScannedDevice)
}