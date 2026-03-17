package com.ledvance.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.snap
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/18/25 14:47
 * Describe : LDVAnimatedVisibility
 */
@Composable
fun LDVAnimatedVisibility(
    visible: Boolean,
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    AnimatedVisibility(
        visible = visible,
        modifier = modifier,
        enter = fadeIn(tween(delayMillis = 200)),
        exit = fadeOut(snap()),
    ) {
        Column(
            content = content,
            verticalArrangement = verticalArrangement,
            horizontalAlignment = horizontalAlignment
        )
    }
}