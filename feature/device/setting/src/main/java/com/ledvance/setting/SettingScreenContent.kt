package com.ledvance.setting

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 15:35
 * Describe : SettingScreenContent
 */
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ledvance.ui.CardView
import com.ledvance.ui.component.ItemView
import com.ledvance.ui.component.LedvanceButton
import com.ledvance.ui.theme.AppTheme
import com.ledvance.ui.R

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
        TitleItem(stringResource(R.string.setting_basic_info))
        CardView {
            ItemView(
                title = stringResource(R.string.setting_device_name),
                content = uiState.deviceName,
                showDivider = true
            )
            ItemView(
                title = stringResource(R.string.setting_device_mac),
                content = uiState.deviceMacAddress,
                showDivider = true
            )
            ItemView(
                title = stringResource(R.string.setting_device_type),
                content = uiState.deviceTypeName,
                showDivider = true
            )
            ItemView(
                title = stringResource(R.string.setting_device_icon),
                contentIconResId = uiState.deviceIconResId,
            )

        }

        TitleItem(stringResource(R.string.setting_function))
        CardView {
            ItemView(
                title = stringResource(R.string.setting_line_sequence),
                content = uiState.lineSequence.title,
                showDivider = true,
                onContentClick = onLineSequenceClick
            )
            ItemView(
                title = stringResource(R.string.setting_reset),
                onContentClick = onResetClick
            )
        }

        TitleItem(stringResource(R.string.setting_firmware))
        CardView {
            ItemView(
                title = stringResource(R.string.setting_firmware_current),
                content = uiState.firmwareVersion,
                showDivider = true,
            )
            ItemView(
                title = stringResource(R.string.setting_firmware_latest),
                content = uiState.firmwareVersion,
                showDivider = true,
                onContentClick = onUpgradeClick
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        LedvanceButton(
            text = stringResource(R.string.setting_delete_device),
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
            .padding(top = 20.dp, bottom = 10.dp, start = 5.dp),
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
    )
}