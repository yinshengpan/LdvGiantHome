package com.ledvance.light.content

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.domain.bean.TimerUiItem
import com.ledvance.domain.bean.command.common.ModeType
import com.ledvance.light.LightDetailsContract
import com.ledvance.light.component.LdvDeviceHeaderCard
import com.ledvance.light.component.LdvModeSwitch
import com.ledvance.ui.CardView
import com.ledvance.ui.R
import com.ledvance.ui.component.LdvBrightnessGradientSlider
import com.ledvance.ui.component.LdvCctGradientSlider
import com.ledvance.ui.component.ScheduleSettingCard
import com.ledvance.ui.extensions.getNameResId
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/2/26 17:49
 * Describe : BedsideDetailScreenContent
 */

@Composable
internal fun BedsideDetailScreenContent(
    deviceName: String,
    deviceIcon: Painter,
    uiState: LightDetailsContract.DetailState.LdvBedsideState,
    onModeChange: (ModeType) -> Unit,
    onPowerChange: (Boolean) -> Unit,
    onTimerChange: (TimerUiItem) -> Unit,
    onCctChange: (Int) -> Unit,
    onBrightnessChange: (Int) -> Unit,
) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 22.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        LdvDeviceHeaderCard(
            switch = uiState.power,
            onSwitchChange = onPowerChange,
            mode = uiState.modeType,
            deviceName = deviceName,
            deviceIcon = deviceIcon,
        )

        LdvModeSwitch(
            selectedMode = uiState.modeType,
            modeList = uiState.modeList,
            onModeTypeChange = onModeChange
        )

        when (uiState.modeType) {
            ModeType.LdvWakeup, ModeType.LdvSleep -> {
                if (uiState.timerList.isNotEmpty()) {
                    TimerListView(
                        timerList = uiState.timerList,
                        onTimerChange = onTimerChange
                    )
                }
            }

            ModeType.LdvFullBright, ModeType.LdvEyeProtection -> {
                SliderControlView(
                    brightness = uiState.brightness,
                    cct = uiState.cct,
                    onBrightnessChange = onBrightnessChange,
                    onCctChange = onCctChange,
                )
            }

            else -> {}
        }
    }

}

@Composable
private fun TimerListView(
    timerList: List<TimerUiItem>,
    onTimerChange: (TimerUiItem) -> Unit,
) {

    Spacer(modifier = Modifier.height(12.5.dp))
    timerList.forEach { timer ->
        CardView(paddingValues = PaddingValues(vertical = 12.5.dp)) {
            ScheduleSettingCard(
                title = stringResource(timer.timerType.getNameResId()),
                icon = painterResource(R.mipmap.icon_timer),
                switch = timer.enabled,
                onSwitchChange = { onTimerChange(timer.copy(enabled = it)) },
                hour = timer.hour,
                minute = timer.minute,
                displayTime = timer.displayTime,
                onTimeChange = { h, m -> onTimerChange(timer.copy(hour = h, minute = m)) },
                repeatDays = timer.days,
                displayRepeat = timer.displayRepeat,
                onRepeatChange = { days -> onTimerChange(timer.copy(days = days)) },
                delay = timer.delay,
                displayDelay = "${timer.delay} min",
                onDelayChange = { delay -> onTimerChange(timer.copy(delay = delay)) }
            )
        }
    }
    Spacer(modifier = Modifier.height(12.5.dp))
}

@Composable
private fun SliderControlView(
    brightness: Int,
    cct: Int,
    cctRange: IntRange = 1800..6500,
    onBrightnessChange: (Int) -> Unit,
    onCctChange: (Int) -> Unit,
) {
    SliderControlCard(
        modifier = Modifier.padding(top = 10.dp),
        title = stringResource(R.string.brightness),
        icon = painterResource(R.drawable.ic_brightness),
        value = brightness,
        unit = "%"
    ) {
        LdvBrightnessGradientSlider(value = brightness, onValueChange = onBrightnessChange)
    }

    SliderControlCard(
        modifier = Modifier.padding(bottom = 10.dp),
        title = stringResource(R.string.cct),
        icon = painterResource(R.drawable.ic_cct),
        value = cct.coerceIn(cctRange),
        unit = "K"
    ) {
        LdvCctGradientSlider(value = cct.coerceIn(cctRange), valueRange = cctRange, onValueChange = onCctChange)
    }
}


@Composable
private fun SliderControlCard(
    title: String,
    icon: Painter,
    value: Int,
    unit: String,
    modifier: Modifier = Modifier,
    slideView: @Composable () -> Unit,
) {
    CardView(modifier = Modifier.then(modifier), paddingValues = PaddingValues(vertical = 10.dp)) {
        Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 35.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = icon,
                    contentDescription = null,
                    modifier = Modifier.size(26.dp)
                )
                Text(
                    text = title, modifier = Modifier.padding(start = 10.dp),
                    style = AppTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    color = AppTheme.colors.title
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = "$value$unit",
                    style = AppTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.W700,
                    ),
                    color = Color(0xFFFFA06F)
                )
            }
            Spacer(modifier = Modifier.height(30.dp))
            slideView()
        }
    }
}