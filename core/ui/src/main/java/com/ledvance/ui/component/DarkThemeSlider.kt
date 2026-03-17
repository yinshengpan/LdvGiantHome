package com.ledvance.ui.component

import android.util.Log
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import com.ledvance.ui.R
import com.ledvance.ui.extensions.toPx
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun DarkThemeSlider(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean,
    trackWidth: Dp = 56.dp,
    thumbSize: Dp = 29.dp,
    lightPainter: Painter = painterResource(R.mipmap.icon_light),
    darkPainter: Painter = painterResource(R.mipmap.icon_dark),
    borderWidth: Dp = 1.dp,
    borderColor: Color = Color.Black,
    thumbColor: Color = Color.White,
    backgroundColor: Color = Color.Black,
    onThemeChange: (Boolean) -> Unit
) {
    val trackHeight = thumbSize
    val thumbSizePx = thumbSize.toPx()
    val trackWidthPx = trackWidth.toPx()
    val dragRangePx = trackWidthPx - thumbSizePx

    val scope = rememberCoroutineScope()
    val animOffset = remember(isDarkTheme) { Animatable(if (isDarkTheme) 1f else 0f) }

    val dragState = rememberDraggableState { delta ->
        val newOffset = (animOffset.value + delta / dragRangePx).coerceIn(0f, 1f)
        scope.launch { animOffset.snapTo(newOffset) }
    }

    Box(
        modifier = modifier
            .size(trackWidth, trackHeight)
            .border(borderWidth, borderColor, CircleShape)
            .clip(CircleShape)
            .background(backgroundColor)
            .drawBehind {
                val thumbCenter = animOffset.value * dragRangePx + thumbSizePx / 2f
                drawRect(
                    color = thumbColor,
                    size = androidx.compose.ui.geometry.Size(thumbCenter, size.height)
                )
            }
            .draggable(
                state = dragState,
                orientation = Orientation.Horizontal,
                onDragStopped = {
                    val target = if (animOffset.value > 0.5f) 1f else 0f
                    scope.launch {
                        animOffset.animateTo(
                            target,
                            spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        )
                        onThemeChange(target == 1f)
                    }
                }
            )
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    val target = if (animOffset.value > 0.5f) 0f else 1f
                    scope.launch {
                        animOffset.animateTo(
                            target,
                            spring(dampingRatio = Spring.DampingRatioMediumBouncy)
                        )
                        onThemeChange(target == 1f)
                    }
                })
            }
    ) {
        val iconPainter = if (animOffset.value < 0.5f) lightPainter else darkPainter
        Box(
            modifier = Modifier
                .size(thumbSize)
                .offset {
                    IntOffset(
                        x = (animOffset.value * dragRangePx).roundToInt(),
                        y = 0
                    )
                }
                .background(thumbColor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = iconPainter,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
