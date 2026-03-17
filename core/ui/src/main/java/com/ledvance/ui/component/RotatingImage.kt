package com.ledvance.ui.component

import androidx.compose.foundation.Image
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.painter.Painter
import kotlinx.coroutines.delay

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/29 09:20
 * Describe : RotatingImage
 */
@Composable
fun RotatingImage(
    painter: Painter,
    modifier: Modifier = Modifier,
    duration: Long = 500,
    isClockwise: Boolean = true
) {
    var rotationState by rememberSaveable { mutableFloatStateOf(0f) }
    LaunchedEffect(duration, isClockwise) {
        val delayValue = 40L
        val speed = (duration.toFloat() / delayValue).let {
            if (isClockwise) it else -it
        }
        while (true) {
            delay(delayValue)
            rotationState = (rotationState + speed) % 360
        }
    }
    Image(
        painter = painter,
        contentDescription = null,
        modifier = modifier.rotate(rotationState)
    )
}