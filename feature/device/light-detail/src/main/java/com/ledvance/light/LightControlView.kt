package com.ledvance.light

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ledvance.domain.bean.WorkMode
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceRadioGroup
import com.ledvance.ui.component.LedvanceSwitch
import com.ledvance.ui.component.workmode.ColourModePicker
import com.ledvance.ui.component.workmode.WhiteModePicker
import com.ledvance.ui.segment.WorkModeSegment
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 09:58
 * Describe : LightControlView
 */
@Composable
internal fun LightControlView(
    switch: Boolean,
    colourModeHue: Int,
    colourModeSat: Int,
    colourModeBrightness: Int,
    whiteModeCct: Int,
    whiteModeBrightness: Int,
    modifier: Modifier = Modifier,
    workMode: WorkMode = WorkMode.Colour,
    isSupportWhiteMode: Boolean = true,
    onWorkModeChange: (WorkMode) -> Unit,
    onColourModeHsChange: (Int, Int) -> Unit,
    onColourModeBrightnessChange: (Int) -> Unit,
    onWhiteModeCctChange: (Int) -> Unit,
    onWhiteModeBrightnessChange: (Int) -> Unit,
    onSwitchChange: (Boolean) -> Unit,
) {
    val allWorkMode = remember { WorkModeSegment.allWorkModeSegment }
    var selectedWorkMode by remember(workMode) {
        mutableStateOf(WorkModeSegment.ofWorkMode(workMode))
    }
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBackground),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(paddingValues = PaddingValues(vertical = 20.dp))
            .then(modifier),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = stringResource(R.string.lighting),
                    style = AppTheme.typography.titleMedium,
                    color = AppTheme.colors.title,
                    modifier = Modifier.weight(1f)
                )
                LedvanceSwitch(
                    checked = switch,
                    onCheckedChange = onSwitchChange
                )
            }

            if (switch) {
                if (isSupportWhiteMode) {
                    LedvanceRadioGroup(
                        selectorItem = selectedWorkMode,
                        items = allWorkMode,
                        modifier = Modifier.padding(top = 15.dp, bottom = 10.dp),
                        shape = RoundedCornerShape(8.dp),
                        checkedColor = Color.White,
                        backgroundColor = AppTheme.colors.divider,
                        checkedTextColor = AppTheme.colors.title,
                        textColor = AppTheme.colors.title,
                        onCheckedChange = {
                            onWorkModeChange(it.value)
                        }
                    )
                } else {
                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (selectedWorkMode == WorkModeSegment.ColorMode) {
                    ColourModePicker(
                        hue = colourModeHue,
                        sat = colourModeSat,
                        brightness = colourModeBrightness,
                        onHsChange = onColourModeHsChange,
                        onBrightnessChange = onColourModeBrightnessChange
                    )
                } else {
                    WhiteModePicker(
                        cct = whiteModeCct,
                        brightness = whiteModeBrightness,
                        onCctChange = { cct, color ->
                            onWhiteModeCctChange(cct)
                        },
                        onBrightnessChange = { brightness ->
                            onWhiteModeBrightnessChange(brightness)
                        },
                    )
                }
            }
        }
    }
}