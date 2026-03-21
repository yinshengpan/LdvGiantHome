package com.ledvance.light

import androidx.compose.runtime.Immutable
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.bean.TimerUiItem
import com.ledvance.domain.bean.TimerType
import com.ledvance.domain.bean.WorkMode
import com.ledvance.domain.bean.command.ModeId
import com.ledvance.domain.bean.command.ModeType
import com.ledvance.domain.bean.command.scenes.Scene
import kotlinx.coroutines.flow.StateFlow
import java.time.DayOfWeek

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:36
 * Describe : LightDetailsContract
 */
internal interface LightDetailsContract {
    @Immutable
    sealed interface UiState {

        @Immutable
        data object Loading : UiState

        @Immutable
        data class Success(
            val deviceName: String,
            val deviceType: DeviceType,
            val isOnline: Boolean,
            val power: Boolean,
            val workMode: WorkMode,
            val colourModeHue: Int,
            val colourModeSat: Int,
            val colourModeBrightness: Int,
            val whiteModeCct: Int,
            val whiteModeBrightness: Int,
            val speed: Int,
            val onTimer: TimerUiItem,
            val offTimer: TimerUiItem,
            val modeType: ModeType?,
            val modeId: ModeId?,
        ) : UiState

        @Immutable
        data object Error : UiState
    }

    val uiState: StateFlow<UiState>

    fun onSwitchChange(switch: Boolean)

    fun onWorkModeChange(workMode: WorkMode)

    fun onColourModeHsChange(hue: Int, sat: Int)
    fun onColourModeBrightnessChange(brightness: Int)

    fun onWhiteModeCctChange(cct: Int)
    fun onWhiteModeBrightnessChange(brightness: Int)
    fun onSceneChange(scene: Scene)
    fun onModeIdChange(modeId: ModeId)
    fun onSpeedChange(speed: Int)

    fun onTimerSwitchChange(timerType: TimerType, enabled: Boolean)
    fun onTimerTimeChange(timerType: TimerType, hour: Int, minute: Int)
    fun onTimerRepeatChange(timerType: TimerType, days: Set<DayOfWeek>)
}