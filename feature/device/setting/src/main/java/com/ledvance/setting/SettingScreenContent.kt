package com.ledvance.setting

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 15:35
 * Describe : SettingScreenContent
 */
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ledvance.ui.component.ItemView
import com.ledvance.ui.component.LedvanceButton
import com.ledvance.ui.theme.AppTheme

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
            ItemView(
                title = "Device Name",
                content = uiState.deviceName,
                showDivider = true
            )
            ItemView(
                title = "Device Mac",
                content = uiState.deviceMacAddress,
                showDivider = true
            )
            ItemView(
                title = "Device Type",
                content = uiState.deviceTypeName,
                showDivider = true
            )
            ItemView(
                title = "Device Icon",
                contentIconResId = uiState.deviceIconResId,
            )

        }

        TitleItem("Function")
        CardLayout {
            ItemView(
                title = "Line Sequence",
                content = uiState.lineSequence.title,
                showDivider = true,
                onContentClick = onLineSequenceClick
            )
            ItemView(
                title = "Reset",
                onContentClick = onResetClick
            )
        }

        TitleItem("Firmware")
        CardLayout {
            ItemView(
                title = "Current Version",
                content = uiState.firmwareVersion,
                showDivider = true,
            )
            ItemView(
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
private fun CardLayout(content: @Composable ColumnScope.() -> Unit) {
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.screenBackground),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(paddingValues = PaddingValues(vertical = 10.dp)),
        content = content
    )
}