package com.ledvance.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ledvance.ui.R
import com.ledvance.ui.component.rectpicker.BrightnessSlider

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 09:00
 * Describe : SpeedSlider
 */
@Composable
fun SpeedSlider(speed: Int, onSpeedChange: (Int) -> Unit) {
    BrightnessSlider(
        modifier = Modifier
            .fillMaxWidth()
            .height(45.dp),
        value = speed,
        valueRange = 0..100,
        minValue = 0,
        iconResId = R.drawable.ic_speed,
        onValueChange = onSpeedChange,
        onValueComplete = onSpeedChange
    )
}