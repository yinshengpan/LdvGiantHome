package com.ledvance.light.screen.scenes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.domain.bean.DeviceId
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.component.LoadingOverlay
import com.ledvance.ui.component.OfflineBanner

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : ScenesScreen
 */
@Composable
internal fun ScenesScreen(
    deviceId: DeviceId,
    viewModel: ScenesContract = hiltViewModel<ScenesViewModel, ScenesViewModel.Factory>(
        creationCallback = { it.create(deviceId) }
    ),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LedvanceScreen(
        title = "Scenes",
        onBackPressed = onBackClick,
    ) {
        when (uiState) {
            is ScenesContract.UiState.Loading, ScenesContract.UiState.Error -> {}
            is ScenesContract.UiState.Success -> {
                val state = uiState as ScenesContract.UiState.Success
                ScenesScreenContent(
                    uiState = state,
                    onSceneChange = viewModel::onSceneChange,
                    onSceneSegmentChange = viewModel::onSceneSegmentChange,
                    onSpeedChange = viewModel::onSpeedChange,
                )
                LoadingOverlay(visible = state.commandLoading)
                OfflineBanner(
                    visible = !state.isOnline,
                    onReconnectClick = viewModel::onReconnect,
                    modifier = Modifier.align(Alignment.BottomCenter)
                )
            }
        }
    }
}