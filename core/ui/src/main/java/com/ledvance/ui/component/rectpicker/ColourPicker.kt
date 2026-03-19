package com.ledvance.ui.component.rectpicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun ColourPicker(
    modifier: Modifier = Modifier,
    // hue:0~360f,sat:0~1f,brightness:1~100
    initialHsv: Hsv = Hsv(0f, 1f, 100),
    showBrightness: Boolean = true,
    minBrightness: Int = 1,
    maxBrightness: Int = 100,
    onColorChanged: (Hsv) -> Unit = {},
    onColorComplete: (Hsv) -> Unit = {}
) {
    var hsv by remember { mutableStateOf(initialHsv) }
    val currentOnColorChanged by rememberUpdatedState(onColorChanged)
    val currentOnColorComplete by rememberUpdatedState(onColorComplete)
    LaunchedEffect(initialHsv) {
        hsv = initialHsv
    }

    val hueBrush = Brush.horizontalGradient(
        colors = listOf(
            Color.Red, Color.Yellow, Color.Green, Color.Cyan, Color.Blue, Color.Magenta, Color.Red
        )
    )
    val saturationBrush = Brush.verticalGradient(
        colors = listOf(Color.White, Color.Transparent)
    )

    fun coorToValue(coor: Offset, pickerSize: Size, thumbRadius: Float): Hsv {
        val w = pickerSize.width - (thumbRadius * 2)
        val h = pickerSize.height - (thumbRadius * 2)

        val hue = ((coor.x - thumbRadius) / w * 360f).coerceIn(0f, 360f)
        val sat = ((coor.y - thumbRadius) / h).coerceIn(0f, 1f)

        return hsv.copy(hue = hue, saturation = sat)
    }

    fun valueToCoor(value: Hsv, pickerSize: Size, thumbRadius: Float): Offset {
        val w = pickerSize.width - (thumbRadius * 2)
        val h = pickerSize.height - (thumbRadius * 2)

        val x = (value.hue / 360f * w) + thumbRadius
        val y = (value.saturation * h) + thumbRadius

        return Offset(x, y)
    }

    Column(modifier = modifier.fillMaxSize()) {
        RectPicker(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            value = hsv,
            onValueChange = { newHsv ->
                hsv = newHsv
                currentOnColorChanged(hsv)
            },
            onRelease = { newHsv ->
                currentOnColorComplete(newHsv)
            },
            valueToCoor = ::valueToCoor,
            coorToValue = ::coorToValue,
            valueToColor = { hsv, _, _ -> ColorUtils.hsvToColor(hsv) },
            backgroundBrushes = listOf(hueBrush, saturationBrush)
        )
        if (showBrightness) {
            BrightnessSlider(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(45.dp),
                value = hsv.brightness.coerceIn(minBrightness, maxBrightness),
                valueRange = minBrightness..maxBrightness,
                onValueChange = { newBrightness ->
                    hsv = hsv.copy(brightness = newBrightness)
                    currentOnColorChanged(hsv)
                },
                onValueComplete = {
                    hsv = hsv.copy(brightness = it)
                    currentOnColorComplete(hsv)
                }
            )
        }
    }
}