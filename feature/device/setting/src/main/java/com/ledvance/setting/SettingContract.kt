package com.ledvance.setting

import androidx.compose.runtime.Immutable
import com.ledvance.domain.FirmwareVersion
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.command.giant.LineSequence
import com.ledvance.ui.utils.OneTimeAction
import com.ledvance.ui.utils.OneTimeActionConsumerContract
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:36
 * Describe : SettingContract
 */
internal interface SettingContract :
    OneTimeActionConsumerContract<SettingContract.SettingOneTimeAction> {

    sealed interface SettingOneTimeAction : OneTimeAction {
        data object DeleteSuccess : SettingOneTimeAction
    }

    @Immutable
    sealed interface UiState {

        @Immutable
        data object Loading : UiState

        @Immutable
        data class Success(
            val isOnline: Boolean,
            val deviceId: DeviceId,
            val loading: Boolean,
            val deviceName: String,
            val deviceMacAddress: String,
            val deviceTypeName: String,
            val deviceIconResId: Int,
            val lineSequence: LineSequence,
            val firmwareVersion: FirmwareVersion,
            val latestFirmwareVersion: FirmwareVersion
        ) : UiState

        @Immutable
        data object Error : UiState
    }

    val uiState: StateFlow<UiState>

    fun resetDevice()
    fun setLineSequence(lineSequence: LineSequence)
    fun onReconnect()
    fun deleteDevice()
}