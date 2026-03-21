package com.ledvance.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.domain.bean.DeviceId
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.component.OfflineBanner
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:53
 * Describe : SettingScreen
 */
@Composable
internal fun SettingScreen(
    deviceId: DeviceId,
    viewModel: SettingContract = hiltViewModel<SettingViewModel, SettingViewModel.Factory>(creationCallback = {
        it.create(deviceId = deviceId)
    }),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LedvanceScreen(
        topBarContainerColor = AppTheme.colors.primaryBackground,
        topBarContentColor = AppTheme.colors.primaryContent,
        horizontalAlignment = Alignment.CenterHorizontally,
        onBackPressed = onBackClick,
        verticalArrangement = Arrangement.Center,
        title = "Setting",
    ) {
        when (uiState) {
            SettingContract.UiState.Error -> {}
            SettingContract.UiState.Loading -> {}
            is SettingContract.UiState.Success -> {
                SettingScreenContent(uiState as SettingContract.UiState.Success)

                val isOnline = (uiState as SettingContract.UiState.Success).isOnline
                OfflineBanner(
                    visible = !isOnline,
                    onReconnectClick = { viewModel.onReconnect() },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}