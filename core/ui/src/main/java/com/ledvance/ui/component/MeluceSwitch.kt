package com.ledvance.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/4/4 11:09
 * Describe : MeluceSwitch with custom dimensions and gradient
 */
@Composable
fun MeluceSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val switchWidth = 44.3.dp
    val switchHeight = 22.2.dp
    val thumbSize = 14.8.dp
    val padding = 3.7.dp

    val colorStart by animateColorAsState(
        targetValue = if (checked) Color(0xFFFF5E6D) else Color(0xFFECE7E6),
        label = "colorStart"
    )
    val colorEnd by animateColorAsState(
        targetValue = if (checked) Color(0xFFFFC370) else Color(0xFFECE7E6),
        label = "colorEnd"
    )

    val alignment by animateDpAsState(
        targetValue = if (checked) switchWidth - thumbSize - padding else padding,
        label = "thumbPosition"
    )

    Box(
        modifier = modifier
            .width(switchWidth)
            .height(switchHeight)
            .clip(CircleShape)
            .background(Brush.linearGradient(listOf(colorStart, colorEnd)))
            .clickable(
                enabled = enabled,
                indication = null,
                interactionSource = remember { MutableInteractionSource() }) {
                onCheckedChange(!checked)
            },
        contentAlignment = Alignment.CenterStart
    ) {
        Card(
            shape = CircleShape,
            elevation = CardDefaults.elevatedCardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier
                .offset(x = alignment)
                .size(thumbSize)
                .clip(CircleShape),
            content = {}
        )
    }
}

@Preview
@Composable
private fun MeluceSwitchPreview() {
    Box(modifier = Modifier.padding(20.dp)) {
        MeluceSwitch(checked = true, onCheckedChange = {})
    }
}

@Preview
@Composable
private fun MeluceSwitchPreviewOff() {
    Box(modifier = Modifier.padding(20.dp)) {
        MeluceSwitch(checked = false, onCheckedChange = {})
    }
}
