package com.ledvance.timer

import androidx.compose.runtime.Immutable
import com.ledvance.domain.bean.command.common.TimerType
import com.ledvance.domain.bean.TimerUiItem
import kotlinx.coroutines.flow.StateFlow
import java.time.DayOfWeek

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
            val onTimer: TimerUiItem,
            val offTimer: TimerUiItem,
            val isOnline: Boolean,
            val loading: Boolean = false
        ) : UiState
        @Immutable
        data object Error : UiState
    }

    val uiState: StateFlow<UiState>

    fun onTimerSwitchChange(timerType: TimerType, enabled: Boolean)
    fun onTimerTimeChange(timerType: TimerType, hour: Int, minute: Int)
    fun onTimerRepeatChange(timerType: TimerType, days: Set<DayOfWeek>)
    fun onReconnect()
}
