package com.ledvance.ui.component.rectpicker

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
internal fun WhitePicker(
    modifier: Modifier = Modifier,
    brightness: Int = 100,
    // 0~100
    cct: Int = 100,
    isSupportCCT: Boolean = true,
    minBrightness: Int = 1,
    maxBrightness: Int = 100,
    onCCTChanged: (Int, Color) -> Unit = { _, _ -> },
    onCCTComplete: (Int, Color) -> Unit = { _, _ -> },
    onBrightnessChanged: (Int) -> Unit = {},
    onBrightnessComplete: (Int) -> Unit = {},
) {
    var useCct by remember(cct) { mutableIntStateOf(cct) }
    val useBrightness by remember(brightness) {
        mutableIntStateOf(brightness.coerceIn(minBrightness, maxBrightness))
    }
    val gradientColors = remember {
        listOf(Color(0xFFFFCA5C), Color.White, Color(0xFFCDECFE))
    }
    var cctColor by remember {
        mutableStateOf(Color.Transparent)
    }
    val tempBrush = Brush.horizontalGradient(
        colors = gradientColors
    )

    fun coorToValue(coor: Offset, pickerSize: Size, thumbRadius: Float): Int {
        val w = pickerSize.width - (thumbRadius * 2)
        val temp = ((coor.x - thumbRadius) / w * 100f).coerceIn(0f, 100f)
        return temp.roundToInt()
    }

    fun valueToCoor(temperature: Int, pickerSize: Size, thumbRadius: Float): Offset {
        val w = pickerSize.width - (thumbRadius * 2)
        val h = pickerSize.height - (thumbRadius * 2)

        val x = (temperature / 100f * w) + thumbRadius
        val y = (pickerSize.height / 2f)

        return Offset(x, y)
    }

    fun brightKelvinToColor(temperature: Int, pickerSize: Size, thumbRadius: Float): Color {
        val w = pickerSize.width - (thumbRadius * 2)
        val offsetX = (temperature / 100f * w)
        if (gradientColors.isEmpty()) return Color.Transparent
        if (gradientColors.size == 1) return gradientColors[0]

        val position = (offsetX / w).coerceIn(0f, 1f)
        val segments = gradientColors.size - 1
        val scaledPos = position * segments
        val index = scaledPos.toInt().coerceAtMost(segments - 1)
        val fraction = scaledPos - index

        val start = gradientColors[index]
        val end = gradientColors[index + 1]

        return lerp(start, end, fraction)
    }

    Column(modifier = modifier.fillMaxSize()) {
        if (isSupportCCT) {
            RectPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                value = useCct,
                onValueChange = {
                    useCct = it
                    onCCTChanged(useCct, cctColor)
                },
                onRelease = {
                    onCCTComplete(it, cctColor)
                },
                valueToCoor = ::valueToCoor,
                coorToValue = ::coorToValue,
                valueToColor = { cct, size, thumbRadius ->
                    cctColor = brightKelvinToColor(cct, size, thumbRadius)
                    cctColor
                },
                backgroundBrushes = listOf(tempBrush)
            )
        }
        BrightnessSlider(
            modifier = Modifier
                .fillMaxWidth()
                .height(45.dp),
            value = useBrightness,
            valueRange = minBrightness..maxBrightness,
            onValueChange = { newBrightness ->
                onBrightnessChanged.invoke(newBrightness)
            },
            onValueComplete = {
                onBrightnessComplete.invoke(it)
            }
        )
    }
}