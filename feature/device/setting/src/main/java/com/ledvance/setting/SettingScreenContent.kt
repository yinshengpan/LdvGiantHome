package com.ledvance.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceSwitch
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 15:35
 * Describe : SettingScreenContent
 */
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.ledvance.ui.component.LedvanceButton

@Composable
internal fun SettingScreenContent(
    uiState: SettingContract.UiState.Success,
    onLineSequenceClick: () -> Unit,
    onResetClick: () -> Unit,
    onUpgradeClick: () -> Unit,
    onDeleteClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        TitleItem("Basic Information")
        CardLayout {
            ContentItem(
                title = "Device Name",
                content = uiState.deviceName,
                showDivider = true
            )
            ContentItem(
                title = "Device Mac",
                content = uiState.deviceMacAddress,
                showDivider = true
            )
            ContentItem(
                title = "Device Type",
                content = uiState.deviceTypeName,
                showDivider = true
            )
            ContentItem(
                title = "Device Icon",
                iconResId = uiState.deviceIconResId,
            )

        }

        TitleItem("Function")
        CardLayout {
            ContentItem(
                title = "Line Sequence",
                content = uiState.lineSequence.title,
                showDivider = true,
                onContentClick = onLineSequenceClick
            )
            ContentItem(
                title = "Reset",
                onContentClick = onResetClick
            )
        }

        TitleItem("Firmware")
        CardLayout {
            ContentItem(
                title = "Current Version",
                content = uiState.firmwareVersion,
                showDivider = true,
            )
            ContentItem(
                title = "Latest Version",
                content = uiState.firmwareVersion,
                showDivider = true,
                onContentClick = onUpgradeClick
            )
        }

        Spacer(modifier = Modifier.height(20.dp))
        
        LedvanceButton(
            text = "Delete Device",
            modifier = Modifier
                .fillMaxWidth(),
            onClick = onDeleteClick
        )
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
private fun TitleItem(title: String) {
    Text(
        text = title, style = AppTheme.typography.titleMedium,
        color = AppTheme.colors.title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
private fun ContentItem(
    title: String,
    content: String? = null,
    iconResId: Int? = null,
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

            iconResId != null -> {
                Image(
                    painter = painterResource(iconResId),
                    contentDescription = null,
                    modifier = Modifier.size(48.dp)
                )
            }

            content != null -> {
                Text(
                    text = content,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.body,
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

@Composable
private fun CardLayout(content: @Composable ColumnScope.() -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.screenBackground),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(paddingValues = PaddingValues(vertical = 10.dp)),
        content = content
    )
}