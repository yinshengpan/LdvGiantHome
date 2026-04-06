package com.ledvance.timer

import androidx.compose.runtime.Immutable
import com.ledvance.domain.bean.TimerUiItem
import kotlinx.coroutines.flow.StateFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : TimerContract
 */
internal interface TimerContract {
    @Immutable
    sealed interface UiState {
        @Immutable
        data object Loading : UiState

        @Immutable
        data class Success(
            val timerList: List<TimerUiItem>,
            val isOnline: Boolean,
            val loading: Boolean = false
        ) : UiState

        @Immutable
        data object Error : UiState
    }

    val uiState: StateFlow<UiState>

    fun onTimerChange(timer: TimerUiItem)
    fun onReconnect()
}
