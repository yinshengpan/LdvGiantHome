package com.ledvance.ui.component.workmode

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.ledvance.ui.component.rectpicker.ColourPicker
import com.ledvance.ui.component.rectpicker.Hsv
import com.ledvance.ui.extensions.clipWithBorder
import com.ledvance.ui.theme.AppTheme
import kotlin.math.roundToInt

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/9/5 11:02
 * Describe : ColorPicker
 */
@Composable
fun ColorModePicker(
    hue: Int,
    sat: Int,
    brightness: Int,
    modifier: Modifier = Modifier,
    borderShape: Shape = RoundedCornerShape(10.dp),
    onHsv: (Int, Int, Int) -> Unit = { _, _, _ -> },
    onHsvComplete: (Int, Int, Int) -> Unit = { _, _, _ -> }
) {
    ColourPicker(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .height(240.dp)
            .clipWithBorder(borderShape, AppTheme.colors.divider, 2.dp),
        initialHsv = Hsv(hue = hue.toFloat(), saturation = sat / 100f, brightness = brightness),
        onColorChanged = {
            val h = it.hue.roundToInt()
            val s = (it.saturation * 100).roundToInt()
            val v = it.brightness
            onHsv.invoke(h, s, v)
        },
        onColorComplete = {
            val h = it.hue.roundToInt()
            val s = (it.saturation * 100).roundToInt()
            val v = it.brightness
            onHsvComplete.invoke(h, s, v)
        })
}