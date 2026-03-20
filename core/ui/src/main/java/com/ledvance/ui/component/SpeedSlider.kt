package com.ledvance.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ledvance.ui.R
import com.ledvance.ui.component.rectpicker.BrightnessSlider
import com.ledvance.ui.extensions.clipWithBorder
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 09:00
 * Describe : SpeedSlider
 */
@Composable
fun SpeedSlider(speed: Int, modifier: Modifier = Modifier, onSpeedChange: (Int) -> Unit) {
    BrightnessSlider(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .height(45.dp)
            .clipWithBorder(RoundedCornerShape(10.dp), AppTheme.colors.divider, 2.dp),
        value = speed,
        valueRange = 0..100,
        minValue = 0,
        iconResId = R.drawable.ic_speed,
        onValueChange = onSpeedChange,
        onValueComplete = onSpeedChange
    )
}