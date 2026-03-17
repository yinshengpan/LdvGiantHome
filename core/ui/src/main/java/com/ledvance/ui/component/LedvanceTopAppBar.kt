package com.ledvance.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/22 16:23
 * Describe : LedvanceTopAppBar
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedvanceTopAppBar(
    modifier: Modifier = Modifier,
    title: String? = null,
    navigationText: String? = null,
    navigationIcon: Painter? = null,
    navigationIconContentDescription: String? = null,
    actionIcon: Painter? = null,
    actionIconContentDescription: String? = null,
    colors: TopAppBarColors = TopAppBarDefaults.centerAlignedTopAppBarColors(),
    onNavigationClick: (() -> Unit)? = {},
    onActionClick: () -> Unit = {},
) {
    CenterAlignedTopAppBar(
        title = {
            title?.also {
                Text(
                    text = title,
                    style = AppTheme.typography.titleLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.widthIn(max = 300.dp)
                )
            }
        },
        navigationIcon = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                onNavigationClick?.takeIf { navigationIcon != null }?.also {
                    IconButton(onClick = onNavigationClick) {
                        Icon(
                            painter = navigationIcon!!,
                            contentDescription = navigationIconContentDescription ?: "",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                navigationText?.also {
                    Text(
                        text = navigationText,
                        style = AppTheme.typography.bodyLarge,
//                        modifier = Modifier.padding(start = if (navigationIcon != null) 0.dp else 15.dp),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.clickable { onNavigationClick?.invoke() }
                    )
                }
            }
        },
        actions = {
            actionIcon?.also {
                IconButton(onClick = onActionClick) {
                    Icon(
                        painter = actionIcon,
                        contentDescription = actionIconContentDescription ?: "",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        },
        colors = colors,
        modifier = modifier.testTag("topAppBar"),
    )
}