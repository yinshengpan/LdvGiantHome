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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
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
    textSize: TextUnit = 20.sp,
    highlightShape: RoundedCornerShape = RoundedCornerShape(12.dp),
    onSelectionChanged: (T) -> Unit = {},
    onPickCompleted: (T) -> Unit = {},
    label: (T) -> String
) {
    val listState = rememberLazyListState(initialIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeight.toPx() }

    // Use derivedStateOf to find the item closest to the center of the viewport
    val centerIndex by remember {
        derivedStateOf {
            val layoutInfo = listState.layoutInfo
            val visibleItems = layoutInfo.visibleItemsInfo
            if (visibleItems.isEmpty()) return@derivedStateOf initialIndex

            val containerCenter = (layoutInfo.viewportEndOffset + layoutInfo.viewportStartOffset) / 2
            visibleItems.minByOrNull { kotlin.math.abs((it.offset + it.size / 2) - containerCenter) }?.index ?: 0
        }
    }

    LaunchedEffect(centerIndex) {
        if (centerIndex in items.indices) {
            onSelectionChanged(items[centerIndex])
        }
    }

    val hasScrolled = remember { mutableStateOf(false) }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            hasScrolled.value = true
        } else if (hasScrolled.value) {
            hasScrolled.value = false
            if (centerIndex in items.indices) {
                onPickCompleted(items[centerIndex])
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
                val distance = kotlin.math.abs(index - centerIndex)

                val alpha = when (distance) {
                    0 -> 1.0f
                    1 -> 0.7f
                    2 -> 0.4f
                    else -> 0.2f
                }
                val scale = when (distance) {
                    0 -> 1.2f
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
                        color = if (distance == 0) textColor else textColor.copy(alpha = alpha),
                        fontSize = textSize,
                        fontWeight = if (distance == 0) FontWeight.Bold else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(horizontal = 30.dp)
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
