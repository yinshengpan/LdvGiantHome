package com.ledvance.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .background(
                    color = AppTheme.colors.screenBackground,
                    shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_ble_disabled),
                contentDescription = "offline",
                modifier = Modifier.size(24.dp),
                tint = AppTheme.colors.primary
            )
            Text(
                text = "Device Offline",
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.title,
                modifier = Modifier.weight(1f)
            )
            Button(
                onClick = onReconnectClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.primaryBackground,
                    contentColor = AppTheme.colors.primaryContent
                ),
                shape = RoundedCornerShape(8.dp),
            ) {
                Text(text = "Reconnect")
            }
        }
    }
}
