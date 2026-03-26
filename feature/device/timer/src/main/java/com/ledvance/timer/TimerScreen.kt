package com.ledvance.timer

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
 * Describe : TimerScreen
 */
@Composable
internal fun TimerScreen(
    deviceId: DeviceId,
    viewModel: TimerContract = hiltViewModel<TimerViewModel, TimerViewModel.Factory>(
        creationCallback = { it.create(deviceId) }
    ),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LedvanceScreen(
        title = stringResource(R.string.title_timer),
        onBackPressed = onBackClick,
        isLoading = (uiState as? TimerContract.UiState.Success)?.loading ?: false
    ) {
        when (uiState) {
            is TimerContract.UiState.Loading, TimerContract.UiState.Error -> {}
            is TimerContract.UiState.Success -> {
                val state = uiState as TimerContract.UiState.Success
                TimerScreenContent(
                    onTimer = state.onTimer,
                    offTimer = state.offTimer,
                    onTimerSwitchChange = viewModel::onTimerSwitchChange,
                    onTimerTimeChange = viewModel::onTimerTimeChange,
                    onTimerRepeatChange = viewModel::onTimerRepeatChange
                )
                OfflineBanner(
                    visible = !state.isOnline,
                    onReconnectClick = viewModel::onReconnect,
                )
            }
        }
    }
}
