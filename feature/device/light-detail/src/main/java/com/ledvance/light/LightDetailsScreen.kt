package com.ledvance.light

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.domain.bean.DeviceId
import com.ledvance.light.component.CardFeature
import com.ledvance.light.content.BedsideDetailScreenContent
import com.ledvance.light.content.GiantDetailScreenContent
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.component.OfflineBanner
import com.ledvance.ui.extensions.getIconResId

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
    onNavigateToFeature: (DeviceId, CardFeature) -> Unit,
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val title = if (uiState is LightDetailsContract.UiState.Success) {
        (uiState as LightDetailsContract.UiState.Success).deviceName
    } else ""
    LedvanceScreen(
        actionIconPainter = painterResource(R.drawable.ic_settings),
        onActionPressed = { onNavigateToSetting.invoke(deviceId) },
        onBackPressed = onBackClick,
        backTitle = "Home",
        enableScroll = true,
        isLoading = (uiState as? LightDetailsContract.UiState.Success)?.loading ?: false
    ) {
        when (uiState) {
            LightDetailsContract.UiState.Error -> {}
            LightDetailsContract.UiState.Loading -> {}
            is LightDetailsContract.UiState.Success -> {
                val state = uiState as LightDetailsContract.UiState.Success
                when (state.detailState) {
                    is LightDetailsContract.DetailState.GiantDetailState -> {
                        GiantDetailScreenContent(
                            uiState = state.detailState,
                            deviceType = state.deviceType,
                            onSwitchChange = { viewModel.onSwitchChange(it) },
                            onWorkModeChange = { viewModel.onWorkModeChange(it) },
                            onColourModeHsChange = { h, s -> viewModel.onColourModeHsChange(h, s) },
                            onColourModeBrightnessChange = { brightness ->
                                viewModel.onColourModeBrightnessChange(
                                    brightness
                                )
                            },
                            onWhiteModeCctChange = { cct -> viewModel.onWhiteModeCctChange(cct) },
                            onWhiteModeBrightnessChange = { brightness ->
                                viewModel.onWhiteModeBrightnessChange(
                                    brightness
                                )
                            },
                            onNavigateToFeature = { feature ->
                                onNavigateToFeature(deviceId, feature)
                            }
                        )
                    }

                    is LightDetailsContract.DetailState.LdvBedsideState -> {
                        BedsideDetailScreenContent(
                            uiState = state.detailState,
                            deviceName = state.deviceName,
                            deviceIcon = painterResource(state.deviceType.getIconResId()),
                            onModeChange = { viewModel.onModeChange(it) },
                            onCctChange = { viewModel.onWhiteModeCctChange(it) },
                            onBrightnessChange = { viewModel.onWhiteModeBrightnessChange(it) },
                            onTimerChange = { viewModel.onTimerChange(it) },
                            onPowerChange = { viewModel.onSwitchChange(it) }
                        )
                    }
                }

                OfflineBanner(
                    visible = !state.isOnline,
                    onReconnectClick = { viewModel.onReconnect() },
                )
            }
        }
    }
}