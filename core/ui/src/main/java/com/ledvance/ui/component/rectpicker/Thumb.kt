package com.ledvance.ui.component.rectpicker

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import com.ledvance.ui.extensions.toPx
import kotlin.math.roundToInt

@Composable
internal fun Thumb(
    position: Offset,
    color: Color,
    size: Dp,
    thumbPainter: Painter?,
    modifier: Modifier = Modifier
) {
    val thumbRadius = size.toPx / 2f
    Box(
        modifier = modifier
            .offset {
                IntOffset(
                    (position.x - thumbRadius).roundToInt(),
                    (position.y - thumbRadius).roundToInt()
                )
            }
            .size(size)
            .clip(CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Box(
            modifier = Modifier
                .size(size * 0.96f)
                .clip(CircleShape)
                .background(color)
        )
        thumbPainter?.let {
            Image(
                painter = it,
                contentDescription = "Thumb Overlay",
                modifier = Modifier.size(size),
                contentScale = ContentScale.FillWidth
            )
        }
    }
}