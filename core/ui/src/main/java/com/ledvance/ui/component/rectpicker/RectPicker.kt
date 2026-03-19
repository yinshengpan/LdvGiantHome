package com.ledvance.ui.component.rectpicker

import android.annotation.SuppressLint
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ledvance.ui.R
import com.ledvance.ui.extensions.toPx
import kotlinx.coroutines.delay
import kotlin.math.pow
import kotlin.math.sqrt

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
internal fun <T> RectPicker(
    value: T,
    onValueChange: (T) -> Unit,
    valueToCoor: (value: T, pickerSize: Size, thumbRadius: Float) -> Offset,
    coorToValue: (coor: Offset, pickerSize: Size, thumbRadius: Float) -> T,
    valueToColor: (value: T, pickerSize: Size, thumbRadius: Float) -> Color,
    backgroundBrushes: List<Brush>,
    modifier: Modifier = Modifier,
    thumbSize: Dp = 42.dp,
    onGrant: (() -> Unit)? = null,
    onRelease: ((T) -> Unit)? = null,
) {
    var pickerSize by remember { mutableStateOf(Size.Zero) }
    var isDragging by remember { mutableStateOf(false) }
    val thumbRadius = thumbSize.toPx / 2f
    var currentPosition by remember { mutableStateOf(Offset.Zero) }
    var isUserHandled by remember { mutableStateOf(false) }

    LaunchedEffect(pickerSize, value) {
        if (!isUserHandled && !isDragging && pickerSize != Size.Zero) {
            currentPosition = valueToCoor(value, pickerSize, thumbRadius)
        }
    }

    LaunchedEffect(isUserHandled) {
        delay(100)
        isUserHandled = false
    }

    BoxWithConstraints(modifier = modifier) {
        pickerSize = Size(constraints.maxWidth.toFloat(), constraints.maxHeight.toFloat())
        val gestureModifier = Modifier
            .pointerInput(pickerSize) {
                fun updateFromGesture(offset: Offset) {
                    val clampedX = offset.x.coerceIn(thumbRadius, pickerSize.width - thumbRadius)
                    val clampedY = offset.y.coerceIn(thumbRadius, pickerSize.height - thumbRadius)
                    val newPos = Offset(clampedX, clampedY)

                    currentPosition = newPos
                    val newValue = coorToValue(newPos, pickerSize, thumbRadius)
                    onValueChange(newValue)
                }

                detectTapGestures(
                    onPress = { offset ->
                        onGrant?.invoke()
                        updateFromGesture(offset)
                        awaitRelease()
                        isUserHandled = true
                        onRelease?.invoke(coorToValue(currentPosition, pickerSize, thumbRadius))
                    }
                )
            }
            .pointerInput(pickerSize) {
                fun updateFromGesture(offset: Offset) {
                    val clampedX = offset.x.coerceIn(thumbRadius, pickerSize.width - thumbRadius)
                    val clampedY = offset.y.coerceIn(thumbRadius, pickerSize.height - thumbRadius)
                    val newPos = Offset(clampedX, clampedY)
                    currentPosition = newPos
                    val newValue = coorToValue(newPos, pickerSize, thumbRadius)
                    onValueChange(newValue)
                }

                detectDragGestures(
                    onDragStart = { offset ->
                        val dist = sqrt(
                            (offset.x - currentPosition.x).pow(2) + (offset.y - currentPosition.y).pow(
                                2
                            )
                        )
                        if (dist <= thumbRadius * 2) { // Allow dragging if near the thumb
                            isDragging = true
                            onGrant?.invoke()
                        }
                    },
                    onDrag = { change, _ ->
                        if (isDragging) {
                            updateFromGesture(change.position)
                        }
                    },
                    onDragEnd = {
                        if (isDragging) {
                            isUserHandled = true
                            onRelease?.invoke(coorToValue(currentPosition, pickerSize, thumbRadius))
                            isDragging = false
                        }
                    },
                    onDragCancel = {
                        if (isDragging) {
                            isUserHandled = true
                            onRelease?.invoke(coorToValue(currentPosition, pickerSize, thumbRadius))
                            isDragging = false
                        }
                    }
                )
            }

        Box(
            modifier = gestureModifier
                .fillMaxSize()
                .drawBehind {
                    backgroundBrushes.forEach { brush ->
                        drawRect(brush = brush)
                    }
                }
        ) {
            if (pickerSize != Size.Zero && currentPosition != Offset.Zero) {
                Thumb(
                    position = currentPosition,
                    color = valueToColor(value, pickerSize, thumbRadius),
                    size = thumbSize,
                    thumbPainter = painterResource(R.mipmap.icon_thumb_mask)
                )
            }
        }
    }
}