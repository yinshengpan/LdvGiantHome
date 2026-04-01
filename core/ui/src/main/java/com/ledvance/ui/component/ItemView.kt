package com.ledvance.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ledvance.ui.R
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/22 19:10
 * Describe : ItemView
 */
@Composable
fun ItemView(
    title: String,
    content: String? = null,
    itemIconResId: Int? = null,
    contentIconResId: Int? = null,
    showDivider: Boolean = false,
    switch: Boolean? = null,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onContentClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .height(54.dp)
            .debouncedClickable(enable = onContentClick != null, onClick = {
                onContentClick?.invoke()
            }),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        if (itemIconResId != null) {
            Image(
                painter = painterResource(itemIconResId),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 15.dp)
                    .size(28.dp)
            )
        }
        Text(
            text = title, style = AppTheme.typography.titleSmall,
            color = AppTheme.colors.title,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when {
            switch != null && onSwitchChange != null -> {
                LedvanceSwitch(switch, onSwitchChange)
            }

            contentIconResId != null -> {
                Image(
                    painter = painterResource(contentIconResId),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }

            content != null -> {
                Text(
                    text = content,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.body,
                    modifier = Modifier.widthIn(max = 200.dp),
                    maxLines = 2,
                    textAlign = TextAlign.End,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        if (onContentClick != null) {
            Image(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier.size(20.dp),
                colorFilter = ColorFilter.tint(AppTheme.colors.body)
            )
        }
    }
    if (showDivider) {
        HorizontalDivider(thickness = 0.5.dp, color = AppTheme.colors.divider)
    }
}