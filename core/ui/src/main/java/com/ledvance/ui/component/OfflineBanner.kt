package com.ledvance.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import com.ledvance.ui.R
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/21/26 19:30
 * Describe : OfflineBanner - 设备离线浮窗，可点击重连
 */

@Composable
fun BoxScope.OfflineBanner(
    visible: Boolean,
    onReconnectClick: () -> Unit,
) {
    OfflineBanner(
        visible = visible,
        onReconnectClick = onReconnectClick,
        modifier = Modifier.align(Alignment.TopCenter)
    )
}

@Composable
fun OfflineBanner(
    visible: Boolean,
    onReconnectClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AnimatedVisibility(
        visible = visible,
        enter = slideInVertically { it },
        exit = slideOutVertically { it },
        modifier = modifier,
    ) {
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.device_offline))
                val openLink = LinkAnnotation.Clickable(
                    tag = stringResource(R.string.reconnect), styles = TextLinkStyles(
                        style = SpanStyle(
                            color = Color(0xFF0D6EFD),
                            textDecoration = TextDecoration.Underline
                        )
                    ),
                    linkInteractionListener = { onReconnectClick.invoke() }
                )
                withLink(openLink) {
                    append("\t")
                    append(stringResource(R.string.reconnect))
                }
            },
            style = AppTheme.typography.bodyMedium,
            modifier = Modifier
                .background(Color(0xFFFFF3CD).copy(alpha = 0.95f))
                .padding(horizontal = 24.dp, vertical = 6.dp)
                .fillMaxWidth(),
            color = Color(0xFF856404)
        )
    }
}
