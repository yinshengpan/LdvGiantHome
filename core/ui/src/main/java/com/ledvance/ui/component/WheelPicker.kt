package com.ledvance.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.utils.extensions.tryCatch

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
    textSize: TextUnit = 33.sp,
    highlightShape: RoundedCornerShape = RoundedCornerShape(12.dp),
    onSelectionChanged: (T) -> Unit = {},
    onPickCompleted: (T) -> Unit = {},
    isInfinite: Boolean = false,
    label:@Composable (T) -> String
) {
    val totalItems = if (isInfinite && items.isNotEmpty()) Int.MAX_VALUE else items.size
    val scrollToIndex = remember(items, initialIndex, isInfinite) {
        if (isInfinite && items.isNotEmpty()) {
            (Int.MAX_VALUE / 2) - ((Int.MAX_VALUE / 2) % items.size) + initialIndex
        } else {
            initialIndex
        }
    }

    val listState = rememberLazyListState(scrollToIndex)
    val flingBehavior = rememberSnapFlingBehavior(lazyListState = listState)
    val density = LocalDensity.current
    val itemHeightPx = with(density) { itemHeight.toPx() }
 
    LaunchedEffect(items, initialIndex, isInfinite) {
        if (items.isNotEmpty() && !listState.isScrollInProgress) {
            tryCatch { listState.scrollToItem(scrollToIndex) }
        }
    }

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
        if (items.isNotEmpty()) {
            val index = if (isInfinite) centerIndex % items.size else centerIndex
            if (index in items.indices) {
                onSelectionChanged(items[index])
            }
        }
    }

    val hasScrolled = remember { mutableStateOf(false) }

    LaunchedEffect(listState.isScrollInProgress) {
        if (listState.isScrollInProgress) {
            hasScrolled.value = true
        } else if (hasScrolled.value) {
            hasScrolled.value = false
            if (items.isNotEmpty()) {
                val index = if (isInfinite) centerIndex % items.size else centerIndex
                if (index in items.indices) {
                    onPickCompleted(items[index])
                }
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

        val itemList = items
        LazyColumn(
            state = listState,
            flingBehavior = flingBehavior,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = itemHeight * (visibleItemsCount / 2))
        ) {
            items(totalItems) { index ->
                val actualIndex = if (isInfinite) index % itemList.size else index
                val item = itemList[actualIndex]
                val distance = kotlin.math.abs(index - centerIndex)

                val alpha = when (distance) {
                    0 -> 1.0f
                    1 -> 0.4f
                    2 -> 0.2f
                    else -> 0.1f
                }
                val scale = when (distance) {
                    0 -> 1f
                    1 -> 0.6f
                    2 -> 0.5f
                    else -> 0.4f
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
                        fontWeight = if (distance == 0) FontWeight.W400 else FontWeight.Normal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
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
