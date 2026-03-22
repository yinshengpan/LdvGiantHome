package com.ledvance.ui.component

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme
import com.ledvance.ui.theme.LocalBackgroundTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/22 16:40
 * Describe : LedvanceScreen
 */
@Composable
fun LedvanceScreen(
    modifier: Modifier = Modifier,
    backTitle: String? = null,
    title: String? = null,
    actionIconPainter: Painter? = null,
    actionEnable: Boolean = true,
    onBackPressed: (() -> Unit)? = null,
    onActionPressed: () -> Unit = {},
    leftIconPainter: Painter? = null,
    onLeftIconClick: (() -> Unit)? = null,
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    topBarContainerColor: Color = AppTheme.colors.primaryBackground,
    topBarContentColor: Color = AppTheme.colors.primaryContent,
    content: @Composable BoxScope.() -> Unit
) {
    val bgColor = LocalBackgroundTheme.current.color
    val localFocusManager = LocalFocusManager.current
    val keyBoardState by keyboardAsState()
    LaunchedEffect(keyBoardState) {
        if (keyBoardState == Keyboard.Closed) {
            localFocusManager.clearFocus()
        }
    }
    BackHandler(onBackPressed != null) {
        onBackPressed?.invoke()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .windowInsetsPadding(
                WindowInsets.safeDrawing.only(
                    WindowInsetsSides.Horizontal,
                ),
            )
            .then(modifier),
        horizontalAlignment = horizontalAlignment,
        verticalArrangement = verticalArrangement,
    ) {
        if (onBackPressed != null || title != null || actionIconPainter != null || leftIconPainter != null) {
            LedvanceTopLayout(
                backTitle = backTitle,
                title = title,
                topBarContainerColor = topBarContainerColor,
                topBarContentColor = topBarContentColor,
                onBackClick = onBackPressed,
                rightIconPainter = actionIconPainter,
                onRightIconClick = onActionPressed,
                rightIconEnable = actionEnable,
                leftIconPainter = leftIconPainter,
                onLeftIconClick = onLeftIconClick
            )
        }
        Box(modifier = Modifier.weight(1f), content = content)
    }
}

@Composable
fun LedvancePrimaryScreen(
    title: String = "",
    modifier: Modifier = Modifier,
    actionEnable: Boolean = true,
    actionIconPainter: Painter? = null,
    onActionPressed: () -> Unit = {},
    verticalArrangement: Arrangement.Vertical = Arrangement.Top,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    content: @Composable ColumnScope.() -> Unit
) {
    LedvanceScreen(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = AppTheme.colors.primary)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 80.dp, start = 24.dp, end = 24.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    modifier = Modifier.weight(1f),
                    style = AppTheme.typography.titleLarge.copy(fontSize = 28.sp),
                    color = AppTheme.colors.primaryContent
                )
                if (actionIconPainter != null) {
                    Icon(
                        painter = actionIconPainter,
                        contentDescription = null,
                        tint = AppTheme.colors.primaryContent.copy(if (actionEnable) 1f else 0.3f),
                        modifier = Modifier
                            .size(28.dp)
                            .debouncedClickable(enable = actionEnable) {
                                onActionPressed.invoke()
                            }
                    )
                }
            }
            Column(
                modifier = Modifier
                    .padding(top = 24.dp)
                    .fillMaxSize()
                    .background(
                        color = AppTheme.colors.screenBackground,
                        shape = RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
                    )
                    .then(modifier),
                verticalArrangement = verticalArrangement,
                horizontalAlignment = horizontalAlignment,
                content = content
            )
        }
    }
}