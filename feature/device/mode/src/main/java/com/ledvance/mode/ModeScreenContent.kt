package com.ledvance.mode

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.domain.bean.command.giant.ModeId
import com.ledvance.ui.component.BrightnessSlider
import com.ledvance.ui.component.LedvanceRadioGroup
import com.ledvance.ui.component.SpeedSlider
import com.ledvance.ui.component.WheelPicker
import com.ledvance.ui.extensions.getNameResId
import com.ledvance.ui.segment.ModeSegment
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 18:16
 * Describe : ModeScreenContent
 */
@Composable
internal fun ModeScreenContent(
    uiState: ModeContract.UiState.Success,
    onBrightnessChange: (Int) -> Unit,
    onSpeedChange: (Int) -> Unit,
    onModeChange: (ModeId) -> Unit,
    modifier: Modifier = Modifier
) {
    val allSegments = remember { ModeSegment.allModeSegment }
    var selectedSegment by remember {
        mutableStateOf(ModeSegment.Base)
    }

    val modes by remember(selectedSegment) {
        derivedStateOf { ModeSegment.getModesBySegment(selectedSegment) }
    }

    val initialIndex = remember(modes, uiState.modeId) {
        val index = modes.indexOfFirst { it == uiState.modeId }
        if (index != -1) index else 0
    }

    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBackground),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            BrightnessSlider(
                brightness = uiState.brightness,
                onBrightnessChange = onBrightnessChange,
            )
            SpeedSlider(
                modifier = Modifier.padding(top = 15.dp),
                speed = uiState.speed,
                onSpeedChange = onSpeedChange,
            )
            LedvanceRadioGroup(
                selectorItem = selectedSegment,
                items = allSegments,
                modifier = Modifier.padding(top = 15.dp, bottom = 10.dp),
                shape = RoundedCornerShape(8.dp),
                checkedColor = Color.White,
                itemWidth = 100.dp,
                backgroundColor = AppTheme.colors.divider,
                checkedTextColor = AppTheme.colors.title,
                textColor = AppTheme.colors.title,
                onCheckedChange = {
                    if (it is ModeSegment) {
                        selectedSegment = it
                    }
                }
            )

            WheelPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                items = modes,
                initialIndex = initialIndex,
                highlightColor = Color(0xFFF2F2F2),
                textColor = Color.Black,
                textSize = 12.sp,
                onPickCompleted = { mode ->
                    onModeChange(mode)
                },
                isInfinite = true,
                label = { stringResource(it.getNameResId()) }
            )
        }
    }
}
