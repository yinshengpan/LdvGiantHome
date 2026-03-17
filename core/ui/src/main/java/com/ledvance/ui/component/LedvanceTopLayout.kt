package com.ledvance.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.ui.R
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/4 08:45
 * Describe : LedvanceTitleLayout
 */

@Composable
fun LedvanceTopLayout(
    backTitle: String? = null,
    title: String? = null,
    topBarContentColor: Color? = null,
    topBarContainerColor: Color? = null,
    onBackClick: (() -> Unit)? = null,
    rightIconPainter: Painter? = null,
    onRightIconClick: (() -> Unit)? = null,
    rightIconEnable: Boolean = true,
    leftIconPainter: Painter? = null,
    onLeftIconClick: (() -> Unit)? = null
) {
    val background = topBarContainerColor?.takeIf { it != Color.Unspecified }
        ?: Color.Transparent
    val contentColor = topBarContentColor?.takeIf { it != Color.Unspecified }
        ?: AppTheme.colors.title
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(background)
            .statusBarsPadding()
            .height(48.dp)
            .padding(horizontal = 20.dp),
    ) {
        if (onBackClick != null && leftIconPainter == null) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .debouncedClickable(onClick = { onBackClick?.invoke() })
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_back),
                    contentDescription = "back",
                    modifier = Modifier.size(24.dp).padding(2.dp),
                    tint = contentColor
                )
                Text(
                    text = backTitle ?: "",
                    style = AppTheme.typography.bodyLarge,
                    maxLines = 1,
                    color = contentColor,
                    modifier = Modifier.padding(start = 5.dp),
                    overflow = TextOverflow.Ellipsis,
                )

            }
        }

        if (leftIconPainter != null) {
            Icon(
                painter = leftIconPainter,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterStart)
                    .debouncedClickable {
                        onLeftIconClick?.invoke()
                    }
            )
        }

        Text(
            text = title ?: "",
            color = contentColor,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(horizontal = 25.dp)
        )

        if (rightIconPainter != null) {
            Icon(
                painter = rightIconPainter,
                contentDescription = null,
                tint = contentColor.copy(if (rightIconEnable) 1f else 0.3f),
                modifier = Modifier
                    .size(24.dp)
                    .align(Alignment.CenterEnd)
                    .debouncedClickable(enable = rightIconEnable) {
                        onRightIconClick?.invoke()
                    }
            )
        }
    }
}