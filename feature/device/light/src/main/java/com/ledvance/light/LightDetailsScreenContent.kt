package com.ledvance.light

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.bean.TimerType
import com.ledvance.domain.bean.WorkMode
import com.ledvance.domain.bean.command.scenes.Scene
import com.ledvance.light.component.LightControl
import com.ledvance.light.component.ScenesControl
import com.ledvance.light.component.SpeedControl
import com.ledvance.light.component.TimerControl
import java.time.DayOfWeek

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 15:35
 * Describe : LightDetailsScreenContent
 */
@Composable
internal fun LightDetailsScreenContent(
    uiState: LightDetailsContract.UiState.Success,
    onSwitchChange: (Boolean) -> Unit,
    onWorkModeChange: (WorkMode) -> Unit,
    onColourModeHsChange: (Int, Int) -> Unit,
    onColourModeBrightnessChange: (Int) -> Unit,
    onWhiteModeCctChange: (Int) -> Unit,
    onWhiteModeBrightnessChange: (Int) -> Unit,
    onClickScene: (Scene) -> Unit,
    onSpeedChange: (Int) -> Unit,
    onTimerSwitchChange: (TimerType, Boolean) -> Unit,
    onTimerTimeChange: (TimerType, Int, Int) -> Unit,
    onTimerRepeatChange: (TimerType, Set<DayOfWeek>) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp),
    ) {
        LightControl(
            switch = uiState.power,
            workMode = uiState.workMode,
            colourModeHue = uiState.colourModeHue,
            colourModeSat = uiState.colourModeSat,
            colourModeBrightness = uiState.colourModeBrightness,
            whiteModeCct = uiState.whiteModeCct,
            whiteModeBrightness = uiState.whiteModeBrightness,
            onSwitchChange = onSwitchChange,
            onWorkModeChange = onWorkModeChange,
            onColourModeHsChange = onColourModeHsChange,
            onColourModeBrightnessChange = onColourModeBrightnessChange,
            onWhiteModeCctChange = onWhiteModeCctChange,
            onWhiteModeBrightnessChange = onWhiteModeBrightnessChange,
        )

        if (uiState.deviceType == DeviceType.Table) {
            ScenesControl(onClickScene = onClickScene)
        }

        SpeedControl(uiState.speed, onSpeedChange = onSpeedChange)

        TimerControl(
            onTimer = uiState.onTimer,
            offTimer = uiState.offTimer,
            onTimerSwitchChange = onTimerSwitchChange,
            onTimerTimeChange = onTimerTimeChange,
            onTimerRepeatChange = onTimerRepeatChange,
        )
    }
}