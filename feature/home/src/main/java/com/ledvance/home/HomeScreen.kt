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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.ledvance.domain.bean.DeviceId
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.dialog.LedvanceDialog
import com.ledvance.ui.state.rememberBluetoothBusinessState
import com.ledvance.ui.theme.AppTheme
import com.ledvance.utils.BluetoothManager

@Composable
internal fun HomeScreen(
    viewModel: HomeContract = hiltViewModel<HomeViewModel>(),
    onToAddNewDevice: () -> Unit,
    onNavigateToControlPanel: (DeviceId) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bluetoothEnableState by BluetoothManager.bluetoothEnableState.collectAsStateWithLifecycle()
    var bluetoothPermission by remember { mutableStateOf(false) }
    val bluetoothBusinessState = rememberBluetoothBusinessState()
    var isResumed by remember { mutableStateOf(false) }
    var deviceToDelete by remember { mutableStateOf<DeviceId?>(null) }

    LifecycleResumeEffect(Unit) {
        bluetoothPermission = bluetoothBusinessState.hasAllow()
        isResumed = true
        onPauseOrDispose {
            isResumed = false
        }
    }

    // 当前台 + 数据就绪时触发连接，无论谁先到达
    LaunchedEffect(uiState, isResumed) {
        val currentState = uiState
        if (isResumed && currentState is HomeContract.UiState.Success) {
            viewModel.connectDevices(currentState.devices.take(3))
        }
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
                HomeScreenContent(
                    uiState = successUiState,
                    onSwitchChange = { deviceId, switch ->
                        viewModel.onSwitchChange(deviceId, switch)
                    },
                    onConnectClick = {
                        viewModel.connectDevice(it)
                    },
                    onDisconnectClick = {
                        viewModel.disconnectDevice(it)
                    },
                    onDeviceClick = {
                        onNavigateToControlPanel(it)
                    },
                    onDeleteClick = {
                        deviceToDelete = it
                    }
                )
            }
        }
    }

    if (deviceToDelete != null) {
        LedvanceDialog(
            title = "Delete Device",
            message = "Are you sure you want to delete this device? This action cannot be undone.",
            onCancel = { deviceToDelete = null },
            onConfirm = {
                viewModel.onDeleteDevice(deviceToDelete!!)
                deviceToDelete = null
            }
        )
    }
}