package com.ledvance.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/19 20:30
 * Describe : WheelPicker
 */
@Composable
fun <T> WheelPicker(
    modifier: Modifier = Modifier,
    items: List<T>,
    initialIndex: Int = 0,
    itemHeight: Dp = 48.dp,
    visibleItemsCount: Int = 5,
    highlightColor: Color = Color(0xFFBC00FF),
    textColor: Color = Color.White,
    highlightShape: RoundedCornerShape = RoundedCornerShape(12.dp),
    onSelectionChanged: (T) -> Unit,
    label: (T) -> String
) {
    val listState = rememberLazyListState(initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeight.toPx() }
    
    LaunchedEffect(listState) {
        snapshotFlow { listState.firstVisibleItemIndex }
            .distinctUntilChanged()
            .collect { index ->
                if (index in items.indices) {
                    onSelectionChanged(items[index])
                }
            }
    }

    Box(
        modifier = modifier
            .height(itemHeight * visibleItemsCount)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center
    ) {
        // Highlight background for the middle item
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(itemHeight)
                .padding(horizontal = 8.dp)
                .background(
                    color = highlightColor,
                    shape = highlightShape
                )
        )

        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = itemHeight * (visibleItemsCount / 2))
        ) {
            items(items.size) { index ->
                val item = items[index]
                
                // Calculate distance from the center item for visual effects
                val centerIndex = listState.firstVisibleItemIndex
                val distance = kotlin.math.abs(index - centerIndex)
                val alpha = when (distance) {
                    0 -> 1.0f
                    1 -> 0.6f
                    2 -> 0.3f
                    else -> 0.1f
                }
                val scale = when (distance) {
                    0 -> 1.1f
                    1 -> 1.0f
                    2 -> 0.9f
                    else -> 0.8f
                }

                Box(
                    modifier = Modifier
                        .height(itemHeight)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label(item),
                        color = textColor,
                        fontSize = 20.sp,
                        fontWeight = if (distance == 0) FontWeight.Bold else FontWeight.Normal,
                        modifier = Modifier
                            .alpha(alpha)
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale
                            )
                    )
                }
            }
        }
    }
}
