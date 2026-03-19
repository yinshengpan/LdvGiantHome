package com.ledvance.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:53
 * Describe : HomeScreen
 */
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.ledvance.ui.state.rememberBluetoothBusinessState
import com.ledvance.utils.BluetoothManager

@Composable
internal fun HomeScreen(
    viewModel: HomeContract = hiltViewModel<HomeViewModel>(),
    onToAddNewDevice: () -> Unit,
    onNavigateToControlPanel: (String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bluetoothEnableState by BluetoothManager.bluetoothEnableState.collectAsStateWithLifecycle()
    var bluetoothPermission by remember { mutableStateOf(false) }
    val bluetoothBusinessState = rememberBluetoothBusinessState()

    LifecycleResumeEffect(Unit) {
        bluetoothPermission = bluetoothBusinessState.hasAllow()
        onPauseOrDispose { }
    }

    LedvanceScreen(
        topBarContainerColor = AppTheme.colors.primaryBackground,
        topBarContentColor = AppTheme.colors.primaryContent,
        horizontalAlignment = Alignment.CenterHorizontally,
        actionIconPainter = painterResource(R.drawable.ic_add),
        onActionPressed = onToAddNewDevice,
        verticalArrangement = Arrangement.Center,
        title = "Ldv Home",
    ) {
        when (uiState) {
            HomeContract.UiState.Empty -> {}
            HomeContract.UiState.Loading -> {}
            is HomeContract.UiState.Success -> {
                val successUiState = uiState as HomeContract.UiState.Success
                LaunchedEffect(successUiState.devices) {
                    viewModel.connectDevices(successUiState.devices.take(3))
                }
                HomeScreenContent(
                    uiState = successUiState,
                    onSwitchChange = { device, switch ->
                        viewModel.onSwitchChange(device, switch)
                    },
                    onConnectClick = {
                        viewModel.connectDevice(it.address)
                    },
                    onDisconnectClick = {
                        viewModel.disconnectDevice(it.address)
                    },
                    onDeviceClick = {
                        onNavigateToControlPanel(it.address)
                    },
                )
            }
        }
    }
}