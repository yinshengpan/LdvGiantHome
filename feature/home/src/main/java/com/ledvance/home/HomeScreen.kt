package com.ledvance.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.domain.bean.DeviceId
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceButton
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.dialog.LedvanceDialog
import com.ledvance.ui.extensions.clipWithBorder
import com.ledvance.ui.extensions.debouncedClickable
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
        viewModel.setPageVisibility(true)
        onPauseOrDispose {
            viewModel.setPageVisibility(false)
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

    LedvanceScreen(
        backgroundColorBrush = AppTheme.colors.screenBackgroundBrush,
        modifier = Modifier.statusBarsPadding(),
        isLoading = (uiState as? HomeContract.UiState.Success)?.loading
    ) {
        val successUiState = (uiState as? HomeContract.UiState.Success) ?: return@LedvanceScreen
        Column(modifier = Modifier.fillMaxSize()) {
            HomeHeader(appName = appName, onToAddNewDevice = onToAddNewDevice)
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
                        viewModel.asyncConnectDevice(it)
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

    LedvanceDialog(
        visible = deviceToDelete != null,
        title = stringResource(R.string.dialog_delete_device_title),
        message = stringResource(R.string.dialog_delete_device_message),
        onCancel = { deviceToDelete = null },
        onConfirm = {
            viewModel.onDeleteDevice(deviceToDelete!!)
            deviceToDelete = null
        }
    )
}

@Composable
private fun HomeHeader(appName: String, onToAddNewDevice: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(top = 15.dp)
            .fillMaxWidth()
            .height(44.dp)
            .padding(horizontal = 23.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(R.mipmap.icon_user),
            contentDescription = null,
            modifier = Modifier
                .size(36.8.dp)
                .clipWithBorder(
                    shape = CircleShape,
                    borderWidth = 2.dp,
                    borderColor = Color(0xFFFF976E)
                )
        )
        Text(
            text = appName,
            style = AppTheme.typography.titleMedium.copy(
                fontSize = 22.sp,
                fontWeight = FontWeight.W700,
            ),
            color = Color(0xFFFF976E),
            modifier = Modifier.padding(start = 11.dp)
        )
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(R.mipmap.icon_add),
            contentDescription = "Add",
            contentScale = ContentScale.FillWidth,
            modifier = Modifier
                .size(24.dp)
                .debouncedClickable(onClick = onToAddNewDevice)
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