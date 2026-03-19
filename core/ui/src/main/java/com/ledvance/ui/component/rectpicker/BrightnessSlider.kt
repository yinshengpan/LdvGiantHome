package com.ledvance.ui.component.rectpicker

import android.annotation.SuppressLint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.ui.R
import com.ledvance.ui.extensions.toDp
import kotlin.math.roundToInt

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun BrightnessSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    onValueComplete: (Int) -> Unit = {},
    modifier: Modifier = Modifier,
    valueRange: IntRange = 1..100,
    minValue: Int = 1,
    activeColor: Color = Color.White,
    trackColor: Color = Color(0xFF666666),
) {
    var isDragging by remember { mutableStateOf(false) }
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnValueComplete by rememberUpdatedState(onValueComplete)
    BoxWithConstraints(modifier = modifier) {
        val sliderWidth = constraints.maxWidth.toFloat()

        var progress by remember { mutableFloatStateOf((value - valueRange.first).toFloat() / (valueRange.last - valueRange.first)) }
        var percent by remember { mutableIntStateOf(value) }
        LaunchedEffect(value) {
            progress = (value - valueRange.first).toFloat() / (valueRange.last - valueRange.first)
            percent = value
        }

        val activeWidth = progress * sliderWidth
        val activeWidthDp = activeWidth.toDp

        val iconToShow = when {
            percent > 60 -> R.drawable.ic_brightness_3
            percent > 20 -> R.drawable.ic_brightness_2
            else -> R.drawable.ic_brightness_1
        }

        val gestureModifier = Modifier
            .pointerInput(sliderWidth, valueRange) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        if (isDragging) {
                            val value =
                                (valueRange.first + progress * (valueRange.last - valueRange.first))
                                    .roundToInt().coerceAtLeast(minValue)
                            currentOnValueComplete.invoke(value)
                            percent = value
                            isDragging = false
                        }
                    }, onDragCancel = {
                        if (isDragging) {
                            val value =
                                (valueRange.first + progress * (valueRange.last - valueRange.first))
                                    .roundToInt().coerceAtLeast(minValue)
                            currentOnValueComplete.invoke(value)
                            percent = value
                            isDragging = false
                        }
                    }) { change, _ ->
                    val newRatio = (change.position.x / sliderWidth).coerceIn(0f, 1f)
                    progress = newRatio
                    val value = (valueRange.first + newRatio * (valueRange.last - valueRange.first))
                        .roundToInt().coerceAtLeast(minValue)
                    currentOnValueChange(value)
                    percent = value
                    change.consume()
                }
            }
            .pointerInput(sliderWidth, valueRange) {
                detectTapGestures(onPress = { offset ->
                    val newRatio = (offset.x / sliderWidth).coerceIn(0f, 1f)
                    progress = newRatio
                    val brightness =
                        (valueRange.first + newRatio * (valueRange.last - valueRange.first))
                            .roundToInt().coerceIn(valueRange).coerceAtLeast(minValue)
                    currentOnValueChange(brightness)
                    percent = brightness
                    awaitRelease()
                    currentOnValueComplete.invoke(brightness)
                })
            }

        Canvas(modifier = gestureModifier.fillMaxSize()) {
            drawRect(color = trackColor)
        }

        Box(
            modifier = Modifier
                .width(activeWidthDp)
                .fillMaxHeight()
                .clipToBounds()
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(color = activeColor)
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clipToBounds(),
            contentAlignment = Alignment.CenterStart
        ) {
            SliderContentOverlay(
                iconPainter = painterResource(iconToShow),
                percent = percent,
                color = Color.White,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(HorizontalProgressClip(activeWidth))
                    .clipToBounds(),
                contentAlignment = Alignment.CenterStart
            ) {
                SliderContentOverlay(
                    iconPainter = painterResource(id = iconToShow),
                    percent = percent,
                    color = Color.Black
                )
            }
        }
    }
}

@Composable
private fun SliderContentOverlay(
    iconPainter: Painter,
    percent: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .wrapContentWidth(unbounded = true)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = iconPainter,
            contentDescription = "Brightness",
            tint = color,
            modifier = Modifier.size(32.dp)
        )
        Spacer(Modifier.width(8.dp))
        Text(
            text = "$percent%",
            color = color,
            maxLines = 1,
            style = TextStyle(fontSize = 16.sp)
        )
    }
}

// 自定义 Shape：用于水平方向的裁剪
private class HorizontalProgressClip(private val activeWidthPx: Float) : Shape {
    override fun createOutline(
        size: Size,
        layoutDirection: LayoutDirection,
        density: Density
    ): Outline {
        return Outline.Rectangle(Rect(0f, 0f, activeWidthPx, size.height))
    }
}