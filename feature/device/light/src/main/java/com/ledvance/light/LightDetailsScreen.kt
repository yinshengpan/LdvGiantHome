package com.ledvance.light

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.domain.bean.DeviceId
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.component.OfflineBanner
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:53
 * Describe : LightDetailsScreen
 */
@Composable
internal fun LightDetailsScreen(
    deviceId: DeviceId,
    viewModel: LightDetailsContract = hiltViewModel<LightDetailsViewModel, LightDetailsViewModel.Factory>(
        creationCallback = { it.create(deviceId = deviceId) }),
    onNavigateToSetting: (DeviceId) -> Unit,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val title = if (uiState is LightDetailsContract.UiState.Success) {
        (uiState as LightDetailsContract.UiState.Success).deviceName
    } else ""
    LedvanceScreen(
        topBarContainerColor = AppTheme.colors.primaryBackground,
        topBarContentColor = AppTheme.colors.primaryContent,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        actionIconPainter = painterResource(R.drawable.ic_settings),
        onActionPressed = { onNavigateToSetting.invoke(deviceId) },
        onBackPressed = onBackClick,
        title = title,
    ) {
        when (uiState) {
            LightDetailsContract.UiState.Error -> {}
            LightDetailsContract.UiState.Loading -> {}
            is LightDetailsContract.UiState.Success -> {
                LightDetailsScreenContent(
                    uiState = uiState as LightDetailsContract.UiState.Success,
                    onSwitchChange = { viewModel.onSwitchChange(it) },
                    onWorkModeChange = { viewModel.onWorkModeChange(it) },
                    onColourModeHsChange = { h, s -> viewModel.onColourModeHsChange(h, s) },
                    onColourModeBrightnessChange = { brightness -> viewModel.onColourModeBrightnessChange(brightness) },
                    onWhiteModeCctChange = { cct -> viewModel.onWhiteModeCctChange(cct) },
                    onWhiteModeBrightnessChange = { brightness -> viewModel.onWhiteModeBrightnessChange(brightness) },
                    onSceneChange = { viewModel.onSceneChange(it) },
                    onSpeedChange = { viewModel.onSpeedChange(it) },
                    onTimerTimeChange = { timerType, hour, minutes -> viewModel.onTimerTimeChange(timerType, hour, minutes) },
                    onTimerRepeatChange = { timerType, days -> viewModel.onTimerRepeatChange(timerType, days) },
                    onTimerSwitchChange = { timerType, switch -> viewModel.onTimerSwitchChange(timerType, switch) },
                    onModeIdChange = { modeId -> viewModel.onModeIdChange(modeId) },
                )

                val isOnline = (uiState as LightDetailsContract.UiState.Success).isOnline
                OfflineBanner(
                    visible = !isOnline,
                    onReconnectClick = { viewModel.onReconnect() },
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}