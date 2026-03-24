package com.ledvance.light.screen.scenes

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.domain.bean.DeviceId
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen
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
        title = stringResource(R.string.title_scene),
        onBackPressed = onBackClick,
        isLoading = (uiState as? ScenesContract.UiState.Success)?.loading ?: false
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
                    onBrightnessChange = viewModel::onBrightnessChange,
                )
                OfflineBanner(
                    visible = !state.isOnline,
                    onReconnectClick = viewModel::onReconnect,
                )
            }
        }
    }
}