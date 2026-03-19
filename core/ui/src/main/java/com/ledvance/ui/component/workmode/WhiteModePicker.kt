package com.ledvance.ui.component.workmode

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.ledvance.ui.component.rectpicker.WhitePicker
import com.ledvance.ui.extensions.clipWithBorder
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/9/5 16:02
 * Describe : WhiteModePicker
 */
@Composable
fun WhiteModePicker(
    cct: Int,
    brightness: Int,
    modifier: Modifier = Modifier,
    isSupportCCT: Boolean = true,
    borderShape: Shape = RoundedCornerShape(10.dp),
    onCCTChanged: (Int, Color) -> Unit = { _, _ -> },
    onCCTComplete: (Int, Color) -> Unit = { _, _ -> },
    onBrightnessChanged: (Int) -> Unit = {},
    onBrightnessComplete: (Int) -> Unit = {},
) {
    WhitePicker(
        cct = cct,
        brightness = brightness,
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .then(if (isSupportCCT) Modifier.height(240.dp) else Modifier)
            .clipWithBorder(borderShape, AppTheme.colors.divider, 2.dp),
        isSupportCCT = isSupportCCT,
        onBrightnessChanged = {
            onBrightnessChanged.invoke(it)
        },
        onBrightnessComplete = {
            onBrightnessComplete.invoke(it)
        },
        onCCTChanged = { cct, color ->
            onCCTChanged.invoke(cct, color)
        },
        onCCTComplete = { cct, color ->
            onCCTComplete.invoke(cct, color)
        }
    )
}