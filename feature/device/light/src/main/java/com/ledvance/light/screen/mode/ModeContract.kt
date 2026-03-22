package com.ledvance.light.screen.mode

import androidx.compose.runtime.Immutable
import com.ledvance.domain.bean.command.ModeId
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : ModeContract
 */
internal interface ModeContract {
    @Immutable
    sealed interface UiState {
        @Immutable
        data object Loading : UiState
        @Immutable
        data class Success(
            val modeId: ModeId?,
            val commandLoading: Boolean = false
        ) : UiState
        @Immutable
        data object Error : UiState
    }

    val uiState: StateFlow<UiState>

    fun onModeIdChange(modeId: ModeId)
}
