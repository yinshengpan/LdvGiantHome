package com.ledvance.setting

import androidx.compose.runtime.Immutable
import com.ledvance.domain.bean.command.LineSequence
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:36
 * Describe : SettingContract
 */
internal interface SettingContract {
    @Immutable
    sealed interface UiState {

        @Immutable
        data object Loading : UiState

        @Immutable
        data class Success(
            val isOnline: Boolean,
            val deviceName: String,
            val deviceMacAddress: String,
            val deviceTypeName: String,
            val deviceIconResId: Int,
            val lineSequence: LineSequence,
            val firmwareVersion: String,
            val latestFirmwareVersion: String
        ) : UiState

        @Immutable
        data object Error : UiState
    }

    val uiState: StateFlow<UiState>

    fun resetDevice()
    fun setLineSequence(lineSequence: LineSequence)
    fun upgradeFirmware()
    fun onReconnect()
}