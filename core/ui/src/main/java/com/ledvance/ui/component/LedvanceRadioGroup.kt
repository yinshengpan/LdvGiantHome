package com.ledvance.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ledvance.ui.extensions.clickableNonRipples
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/9/5 11:16
 * Describe : LedvanceRadioGroup
 */
interface IRadioGroupItem<T> {
    val title: String
    val value: T
}

@Composable
fun <T> LedvanceRadioGroup(
    selectorItem: IRadioGroupItem<T>,
    items: List<IRadioGroupItem<T>>,
    modifier: Modifier = Modifier,
    shape: Shape = CircleShape,
    checkedColor: Color = AppTheme.colors.primary,
    backgroundColor: Color = Color.White,
    checkedTextColor: Color = Color.White,
    textColor: Color = AppTheme.colors.title,
    onCheckedChange: (IRadioGroupItem<T>) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier)
            .height(40.dp)
            .background(backgroundColor, shape = shape),
        verticalAlignment = Alignment.CenterVertically
    ) {
        items.forEach {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(2.dp)
                    .fillMaxHeight()
                    .background(
                        color = if (selectorItem.value == it.value) checkedColor else Color.Transparent,
                        shape = shape
                    )
                    .clip(shape)
                    .clickableNonRipples {
                        onCheckedChange(it)
                    }, contentAlignment = Alignment.Center
            ) {
                Text(
                    text = it.title,
                    color = if (selectorItem.value == it.value) checkedTextColor else textColor,
                    style = AppTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}