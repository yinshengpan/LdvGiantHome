package com.ledvance.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.domain.bean.DeviceId
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceButton
import com.ledvance.ui.component.LedvancePrimaryScreen
import com.ledvance.ui.dialog.LedvanceDialog
import com.ledvance.ui.state.rememberBluetoothBusinessState
import com.ledvance.ui.theme.AppTheme
import com.ledvance.utils.BluetoothManager
import com.ledvance.utils.extensions.getAppName

@Composable
internal fun HomeScreen(
    viewModel: HomeContract = hiltViewModel<HomeViewModel>(),
    onToAddNewDevice: () -> Unit,
    onNavigateToControlPanel: (DeviceId) -> Unit,
) {
    val context = LocalContext.current
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
    LaunchedEffect(uiState, isResumed, bluetoothEnableState, bluetoothPermission) {
        val currentState = uiState
        if (isResumed && currentState is HomeContract.UiState.Success
            && bluetoothBusinessState.hasAllow()
        ) {
            viewModel.connectDevices(currentState.devices.take(3))
        }
    }

    val appName = remember { context.getAppName() ?: "" }

    LedvancePrimaryScreen(
        title = appName,
        actionIconPainter = painterResource(R.drawable.ic_add),
        onActionPressed = onToAddNewDevice,
        isLoading = (uiState as? HomeContract.UiState.Success)?.loading ?: false
    ) {
        when (uiState) {
            HomeContract.UiState.Loading -> {}
            is HomeContract.UiState.Success -> {
                val successUiState = uiState as HomeContract.UiState.Success
                if (successUiState.devices.isNotEmpty()) {
                    HomeScreenContent(
                        uiState = successUiState,
                        onSwitchChange = { deviceId, switch ->
                            viewModel.onSwitchChange(deviceId, switch)
                        },
                        onConnectClick = {
                            if (bluetoothBusinessState.hasAllow()) {
                                viewModel.connectDevice(it)
                            }
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
                } else {
                    EmptyData(onToAddNewDevice = onToAddNewDevice)
                }
            }
        }
    }

    if (deviceToDelete != null) {
        LedvanceDialog(
            title = stringResource(R.string.dialog_delete_device_title),
            message = stringResource(R.string.dialog_delete_device_message),
            onCancel = { deviceToDelete = null },
            onConfirm = {
                viewModel.onDeleteDevice(deviceToDelete!!)
                deviceToDelete = null
            }
        )
    }
}

@Composable
private fun EmptyData(onToAddNewDevice: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.empty_devices),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.body
        )
        Spacer(modifier = Modifier.height(24.dp))
        LedvanceButton(
            text = stringResource(R.string.add_device),
            modifier = Modifier.width(150.dp),
            onClick = onToAddNewDevice
        )
    }
}