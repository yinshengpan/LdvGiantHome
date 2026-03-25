package com.ledvance.ota

import androidx.compose.runtime.Immutable
import com.ledvance.domain.FirmwareVersion
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:36
 * Describe : OtaContract
 */
internal interface OtaUpdateContract {
    @Immutable
    sealed interface UiState {

        @Immutable
        data object Loading : UiState

        @Immutable
        data class Success(
            val deviceName: String,
            val currentVersion: FirmwareVersion,
            val latestVersion: FirmwareVersion,
            val otaFileSize: String,
            val otaFilePath: String,
            val updateState: UpdateState,
        ) : UiState
    }

    val uiState: StateFlow<UiState>

    fun startUpdateFirmware()
    fun isOtaUpdating(): Boolean

    @Immutable
    sealed interface UpdateState {
        @Immutable
        data object Idle : UpdateState

        @Immutable
        data object Success : UpdateState

        @Immutable
        data class Progress(val progress: Int) : UpdateState

        @Immutable
        data object Failed : UpdateState
    }
}