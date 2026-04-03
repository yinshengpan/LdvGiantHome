package com.ledvance.ui.component

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun LdvGradientSlider(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    onValueComplete: (Int) -> Unit = {},
    valueRange: IntRange = 1..100,
    minValue: Int = 1,
    gradientColors: List<Color>
) {
    var isDragging by remember { mutableStateOf(false) }
    val currentOnValueChange by rememberUpdatedState(onValueChange)
    val currentOnValueComplete by rememberUpdatedState(onValueComplete)

    BoxWithConstraints(
        modifier = modifier.height(38.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val density = LocalDensity.current
        val trackWidth = constraints.maxWidth.toFloat()
        val trackHeightDp = 24.dp
        val thumbSizeDp = 30.dp
        val thumbMarginDp = 0.dp
        
        val thumbMarginPx = with(density) { thumbMarginDp.toPx() }
        val thumbSizePx = with(density) { thumbSizeDp.toPx() }
        val maxProgressWidth = trackWidth - thumbSizePx - (thumbMarginPx * 2)

        var progress by remember { mutableFloatStateOf((value - valueRange.first).toFloat() / (valueRange.last - valueRange.first)) }

        LaunchedEffect(value) {
            if (!isDragging) {
                val clampedValue = value.coerceIn(valueRange)
                progress = (clampedValue - valueRange.first).toFloat() / (valueRange.last - valueRange.first)
            }
        }

        val gestureModifier = Modifier
            .pointerInput(trackWidth, valueRange) {
                detectHorizontalDragGestures(
                    onDragStart = {
                        isDragging = true
                    },
                    onDragEnd = {
                        if (isDragging) {
                            val computedValue = (valueRange.first + progress * (valueRange.last - valueRange.first))
                                .roundToInt().coerceIn(valueRange).coerceAtLeast(minValue)
                            currentOnValueComplete(computedValue)
                            isDragging = false
                        }
                    },
                    onDragCancel = {
                        if (isDragging) {
                            val computedValue = (valueRange.first + progress * (valueRange.last - valueRange.first))
                                .roundToInt().coerceIn(valueRange).coerceAtLeast(minValue)
                            currentOnValueComplete(computedValue)
                            isDragging = false
                        }
                    }
                ) { change, dragAmount ->
                    val newProgress = (progress + dragAmount / maxProgressWidth).coerceIn(0f, 1f)
                    progress = newProgress
                    val computedValue = (valueRange.first + newProgress * (valueRange.last - valueRange.first))
                        .roundToInt().coerceIn(valueRange).coerceAtLeast(minValue)
                    currentOnValueChange(computedValue)
                    change.consume()
                }
            }
            .pointerInput(trackWidth, valueRange) {
                detectTapGestures(onPress = { offset ->
                    val tapX = offset.x - thumbMarginPx - (thumbSizePx / 2)
                    val newProgress = (tapX / maxProgressWidth).coerceIn(0f, 1f)
                    progress = newProgress
                    val computedValue = (valueRange.first + newProgress * (valueRange.last - valueRange.first))
                        .roundToInt().coerceIn(valueRange).coerceAtLeast(minValue)
                    currentOnValueChange(computedValue)
                    awaitRelease()
                    currentOnValueComplete(computedValue)
                })
            }

        Box(
            modifier = gestureModifier.fillMaxSize(),
            contentAlignment = Alignment.CenterStart
        ) {
            // Background Track
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(trackHeightDp)
                    .clip(RoundedCornerShape(percent = 50))
                    .background(Color(0xFFE3E3E3))
            )
            
            // Active Progress
            val activeWidth = (progress * maxProgressWidth) + thumbSizePx + (thumbMarginPx * 2)
            Box(
                modifier = Modifier
                    .height(trackHeightDp)
                    .width(with(density) { activeWidth.toDp() })
                    .clip(RoundedCornerShape(percent = 50))
                    .background(
                        Brush.horizontalGradient(
                            colors = gradientColors,
                            startX = 0f,
                            endX = trackWidth
                        )
                    )
            )

            // Thumb
            Card(
                modifier = Modifier
                    .offset {
                        IntOffset((thumbMarginPx + progress * maxProgressWidth).roundToInt(), 0)
                    }
                    .size(thumbSizeDp),
                shape = CircleShape,
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {}
        }
    }
}

@Composable
fun LdvBrightnessGradientSlider(
    value: Int,
    modifier: Modifier = Modifier,
    onValueComplete: (Int) -> Unit = {},
    onValueChange: (Int) -> Unit,
) {
    val gradientColors = remember { listOf(Color(0xFFFFF3D8), Color(0xFFFFAF3C)) }
    LdvGradientSlider(
        value = value,
        onValueChange = onValueChange,
        onValueComplete = onValueComplete,
        valueRange = 1..100,
        minValue = 1,
        gradientColors = gradientColors,
        modifier = modifier
    )
}

@Composable
fun LdvCctGradientSlider(
    value: Int,
    modifier: Modifier = Modifier,
    onValueComplete: (Int) -> Unit = {},
    valueRange: IntRange = 1000..8000,
    minValue: Int = 1000,
    onValueChange: (Int) -> Unit,
) {
    val gradientColors = remember { listOf(Color(0xFFCDDFFF), Color(0xFFFFC97B)) }
    LdvGradientSlider(
        value = value,
        onValueChange = onValueChange,
        onValueComplete = onValueComplete,
        valueRange = valueRange,
        minValue = minValue,
        gradientColors = gradientColors,
        modifier = modifier
    )
}
