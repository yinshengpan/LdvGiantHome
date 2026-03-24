package com.ledvance.light.screen.music

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
 * Describe : MusicScreen
 */
@Composable
internal fun MusicScreen(
    deviceId: DeviceId,
    viewModel: MusicContract = hiltViewModel<MusicViewModel, MusicViewModel.Factory>(creationCallback = {
        it.create(deviceId)
    }),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LedvanceScreen(
        title = stringResource(R.string.title_music),
        onBackPressed = onBackClick,
        isLoading = (uiState as? MusicContract.UiState.Success)?.loading ?: false
    ) {
        when (uiState) {
            MusicContract.UiState.Error -> {}
            MusicContract.UiState.Loading -> {}
            is MusicContract.UiState.Success -> {
                val state = uiState as MusicContract.UiState.Success
                MusicScreenContent(
                    uiState = state,
                    onRhythmChange = viewModel::onRhythmChange,
                    onDeviceMicSensitivityChange = viewModel::onDeviceMicSensitivityChange,
                    onPhoneMicSensitivityChange = viewModel::onPhoneMicSensitivityChange,
                    onMusicSegmentChange = viewModel::onMusicSegmentChange,
                )

                OfflineBanner(
                    visible = !state.isOnline,
                    onReconnectClick = viewModel::onReconnect,
                )
            }
        }

    }
}
