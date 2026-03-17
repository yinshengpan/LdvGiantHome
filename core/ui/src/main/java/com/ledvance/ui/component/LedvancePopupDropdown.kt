package com.ledvance.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.onPlaced
import androidx.compose.ui.layout.positionInParent
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ledvance.ui.R
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.extensions.toDp
import com.ledvance.ui.theme.AppTheme


/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/2 16:51
 * Describe : LedvancePopupDropdown
 */
private const val TAG = "LedvancePopupDropdown"

@Composable
fun <T> LedvancePopupDropdown(
    title: String,
    selectedItem: T,
    items: List<T>,
    modifier: Modifier = Modifier,
    onItemSelected: (T) -> Unit,
    itemTitle: @Composable (T) -> String,
    itemEquals: (T, T) -> Boolean = { a, b -> a == b }
) {
    var expanded by remember { mutableStateOf(false) }
    val anchorOffset = remember { mutableStateOf(Offset.Zero) }
    val parentOffset = remember { mutableStateOf(Offset.Zero) }
    val anchorSize = remember { mutableStateOf(IntSize.Zero) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .onPlaced {
                parentOffset.value = it.positionInParent()
            },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = AppTheme.typography.bodySmall,
            color = AppTheme.colors.title,
            modifier = Modifier
                .weight(1f)
                .padding(end = 40.dp)
        )
        Row(
            modifier = Modifier
                .width(155.dp)
                .height(30.dp)
                .then(
                    if (expanded) Modifier.border(
                        width = 1.dp,
                        brush = AppTheme.colors.buttonBorderBrush,
                        shape = RoundedCornerShape(4.dp)
                    )
                    else Modifier.border(
                        width = 1.toDp(),
                        color = AppTheme.colors.textFieldBorder,
                        shape = RoundedCornerShape(4.dp)
                    )
                )
                .onGloballyPositioned { coordinates ->
                    val pos = coordinates.positionInParent()
                    anchorOffset.value = pos
                    anchorSize.value = coordinates.size
                }
                .background(AppTheme.colors.textFieldBackground)
                .clickable {
                    expanded = true
                },
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = itemTitle(selectedItem),
                style = AppTheme.typography.bodySmall,
                maxLines = 1,
                color = AppTheme.colors.textFieldContent,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 15.dp, end = 5.dp)
            )
            Icon(
                painter = painterResource(R.mipmap.icon_arrow_down),
                contentDescription = "",
                modifier = Modifier
                    .padding(end = 15.dp)
                    .rotate(if (expanded) 180f else 0f)
                    .size(11.dp),
                tint = AppTheme.colors.textFieldUnit
            )
        }
    }
    if (expanded) {
        Popup(
            alignment = Alignment.TopStart,
            offset = IntOffset(
                x = anchorOffset.value.x.toInt(),
                y = (parentOffset.value.y + anchorSize.value.height + 1.dp.value).toInt()
            ),
            properties = PopupProperties(
                focusable = true,
                dismissOnClickOutside = true,
                dismissOnBackPress = true
            ),
            onDismissRequest = { expanded = false }
        ) {
            Surface(
                shape = RoundedCornerShape(4.dp),
                shadowElevation = 8.dp,
                modifier = Modifier
                    .width(anchorSize.value.width.toDp())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppTheme.colors.popupBackground)
                ) {
                    items.forEach { item ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(30.dp)
                                .background(
                                    color = if (itemEquals(item, selectedItem)) {
                                        AppTheme.colors.popupSecondaryBackground
                                    } else Color.Transparent,
                                    shape = RoundedCornerShape(4.dp)
                                )
                                .debouncedClickable {
                                    onItemSelected(item)
                                    expanded = false
                                }
                                .padding(horizontal = 15.dp),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            Text(
                                text = itemTitle(item),
                                style = AppTheme.typography.bodySmall,
                                color = AppTheme.colors.textFieldContent,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                    }
                }
            }
        }
    }
}