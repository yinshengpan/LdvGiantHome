package com.ledvance.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/9/4 16:09
 * Describe : LedvanceSwitch
 */
@Composable
fun LedvanceSwitch(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val switchWidth = 50.dp
    val switchHeight = 28.dp
    val thumbSize = 24.dp
    val padding = 2.dp

    val backgroundColor by animateColorAsState(
        targetValue = if (checked) Color(0xFF34C759) else Color(0x28787880),
        label = "switchColor"
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
            .background(backgroundColor)
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