package com.ledvance.light.screen.mode

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.domain.bean.DeviceId
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.component.LoadingOverlay

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
    ) {
        when (uiState) {
            is ModeContract.UiState.Loading, ModeContract.UiState.Error -> {}
            is ModeContract.UiState.Success -> {
                val state = uiState as ModeContract.UiState.Success
                ModeScreenContent(
                    selectedModeId = state.modeId,
                    onModeChange = viewModel::onModeIdChange
                )
                LoadingOverlay(visible = state.commandLoading)
            }
        }
    }
}
