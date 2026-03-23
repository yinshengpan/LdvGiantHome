package com.ledvance.light.screen.mode

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.domain.bean.DeviceId
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.component.OfflineBanner

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : ModeScreen
 */
@Composable
internal fun ModeScreen(
    deviceId: DeviceId,
    viewModel: ModeContract = hiltViewModel<ModeViewModel, ModeViewModel.Factory>(
        creationCallback = { it.create(deviceId) }
    ),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LedvanceScreen(
        title = "Mode",
        onBackPressed = onBackClick,
        isLoading = (uiState as? ModeContract.UiState.Success)?.loading ?: false
    ) {
        when (uiState) {
            is ModeContract.UiState.Loading, ModeContract.UiState.Error -> {}
            is ModeContract.UiState.Success -> {
                val state = uiState as ModeContract.UiState.Success
                ModeScreenContent(
                    uiState = state,
                    onModeChange = viewModel::onModeIdChange,
                    onSpeedChange =  viewModel::onSpeedChange,
                    onBrightnessChange =  viewModel::onBrightnessChange,
                )
                OfflineBanner(
                    visible = !state.isOnline,
                    onReconnectClick = viewModel::onReconnect,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}
