package com.ledvance.light

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:53
 * Describe : LightDetailsScreen
 */
@Composable
internal fun LightDetailsScreen(
    address: String,
    viewModel: LightDetailsContract = hiltViewModel<LightDetailsViewModel, LightDetailsViewModel.Factory>(
        creationCallback = { it.create(address = address) }),
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
        onBackPressed = onBackClick,
        title = title,
        modifier = Modifier.verticalScroll(rememberScrollState())
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
                    onClickScene = { viewModel.onClickScene(it) }
                )
            }
        }
    }
}