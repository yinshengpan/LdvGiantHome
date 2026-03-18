package com.ledvance.energy.manager.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.energy.manager.dialog.LedvanceDialog
import com.ledvance.energy.manager.state.rememberBluetoothBusinessState
import com.ledvance.energy.manager.viewmodel.BleViewModel
import com.ledvance.energy.manager.viewmodel.DeviceListViewModel
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.component.LottieAsset
import com.ledvance.ui.component.scrollbar.DraggableScrollbar
import com.ledvance.ui.component.scrollbar.rememberDraggableScroller
import com.ledvance.ui.component.scrollbar.scrollbarState
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.extensions.stringResourceFormat
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.theme.AppTheme
import com.ledvance.utils.BluetoothManager
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/12/25 11:16
 * Describe : DeviceListScreen
 */
private const val TAG = "DeviceListScreen"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DeviceListScreen(
    bleViewModel: BleViewModel = hiltViewModel(),
    viewModel: DeviceListViewModel = hiltViewModel(),
    onBackPressed: () -> Unit,
    onGotoPage: (NavigationRoute) -> Unit,
) {
    val scope = rememberCoroutineScope()

    val scanDevices by bleViewModel.scanDevices.collectAsStateWithLifecycle()
    val localDevices by viewModel.localDevices.collectAsStateWithLifecycle()
    val bluetoothEnableState by BluetoothManager.bluetoothEnableState.collectAsStateWithLifecycle()
    var bluetoothPermission by remember { mutableStateOf(false) }
    val bluetoothBusinessState by rememberBluetoothBusinessState()
    var currentSelectedDevice by remember { mutableStateOf<ScannedDevice?>(null) }

    LifecycleResumeEffect(Unit) {
        bluetoothPermission = bluetoothBusinessState.hasAllow()
        onPauseOrDispose { }
    }

    DisposableEffect(key1 = bluetoothPermission, key2 = bluetoothEnableState) {
        Timber.tag(TAG)
            .i("bluetoothEnableState -> $bluetoothEnableState,bluetoothPermission -> $bluetoothPermission")
        if (bluetoothEnableState && bluetoothPermission) {
            bleViewModel.startBleScan()
        } else {
            bleViewModel.stopBleScan()
        }

        onDispose {
            bleViewModel.stopBleScan()
        }
    }

    LedvanceScreen(
        topBarContainerColor = AppTheme.colors.primaryBackground,
        topBarContentColor = AppTheme.colors.primaryContent,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        title = stringResource(R.string.device_list),
        onBackPressed = onBackPressed
    ) {
        when {
            scanDevices.isEmpty() && localDevices.isEmpty() -> {
                LottieAsset(
                    assetName = "ble.lottie",
                    modifier = Modifier
                        .padding(top = 76.dp)
                        .align(Alignment.TopCenter)
                )
            }

            else -> {
                DeviceListContent(
                    scanDevices = scanDevices,
                    localDevices = localDevices,
                    onDeleteClick = {
                    },
                    onItemClick = {
                        scope.launch {
                        }
                    })
            }
        }

    }
}


@Composable
private fun DeviceListContent(
    scanDevices: List<ScannedDevice>, localDevices: List<ScannedDevice>,
    onDeleteClick: (ScannedDevice) -> Unit,
    onItemClick: (ScannedDevice) -> Unit,
) {
    val scanDeviceAddressSet by rememberUpdatedState(scanDevices.map { it.address }.toSet())
    val localDeviceAddressSet by rememberUpdatedState(localDevices.map { it.address }.toSet())
    val listState = rememberLazyListState()
    val availableDevices by rememberUpdatedState(scanDevices.filter {
        !localDeviceAddressSet.contains(it.address)
    })
    var showDeleteDeviceDialog by remember { mutableStateOf<ScannedDevice?>(null) }
    var showOfflineDeviceDialog by remember { mutableStateOf<ScannedDevice?>(null) }

    if (showDeleteDeviceDialog != null) {
        LedvanceDialog(
            title = stringResourceFormat(
                R.string.delete_device_dialog_title,
                showDeleteDeviceDialog?.sn ?: ""
            ),
            message = stringResource(R.string.delete_device_dialog_content),
            confirmText = stringResource(R.string.confirm),
            cancelText = stringResource(R.string.cancel),
            maxLines = 3,
            onCancel = {
                showDeleteDeviceDialog = null
            },
            onConfirm = {
                showDeleteDeviceDialog?.also { onDeleteClick.invoke(it) }
                showDeleteDeviceDialog = null
            })
    }
    if (showOfflineDeviceDialog != null) {
        LedvanceDialog(
            title = stringResourceFormat(
                R.string.device_offline_dialog_title,
                showOfflineDeviceDialog?.sn ?: ""
            ),
            message = stringResource(R.string.device_offline_dialog_content),
            confirmText = stringResource(R.string.got_it),
            cancelText = null,
            maxLines = 3,
            onConfirm = {
                showOfflineDeviceDialog = null
            })
    }
    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        LazyColumn(state = listState, modifier = Modifier.padding(top = 10.dp)) {
            if (localDevices.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.paired_energy_controller),
                        style = AppTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        color = AppTheme.colors.primary,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
                    )
                }
                items(items = localDevices, key = { it.address }) {
                    DeviceItem(
                        device = it,
                        isOnline = scanDeviceAddressSet.contains(it.address),
                        onDeleteDevice = { showDeleteDeviceDialog = it },
                        onItemClick = { device, isOnline ->
                            if (!isOnline) {
                                showOfflineDeviceDialog = device
                                return@DeviceItem
                            }
                            onItemClick.invoke(device)
                        },
                    )
                }
            }
            if (availableDevices.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(R.string.available_energy_controller),
                        style = AppTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        color = AppTheme.colors.primary,
                        modifier = Modifier.padding(vertical = 10.dp, horizontal = 20.dp)
                    )
                }
                items(items = availableDevices, key = { it.address }) {
                    DeviceItem(
                        device = it,
                        isOnline = true,
                        onItemClick = { device, _ ->
                            onItemClick.invoke(device)
                        },
                    )
                }
            }
            item {
                Spacer(modifier = Modifier.height(50.dp))
            }
        }
        val size = scanDevices.size
        val scrollbarState = listState.scrollbarState(itemsAvailable = size)
        listState.DraggableScrollbar(
            state = scrollbarState,
            orientation = Orientation.Vertical,
            onThumbMoved = listState.rememberDraggableScroller(
                itemsAvailable = size
            ),
            modifier = Modifier
                .fillMaxHeight()
                .windowInsetsPadding(WindowInsets.systemBars)
                .padding(horizontal = 1.dp)
                .align(Alignment.CenterEnd)
        )
    }
}

@Composable
fun DeviceItem(
    device: ScannedDevice,
    isOnline: Boolean,
    onDeleteDevice: (ScannedDevice) -> Unit = {},
    onItemClick: (ScannedDevice, Boolean) -> Unit = { _, _ -> },
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .fillMaxWidth()
            .debouncedClickable {
                onItemClick(device, isOnline)
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White,
            contentColor = Color.White
        ),
        shape = RoundedCornerShape(7.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .height(80.dp),
        ) {
            Image(
                painter = painterResource(id = R.drawable.bluetooth),
                contentDescription = null,
                colorFilter = ColorFilter.tint(AppTheme.colors.body).takeIf { !isOnline },
                modifier = Modifier.size(28.dp)
            )

            Text(
                text = device.name,
                style = AppTheme.typography.titleSmall.copy(
                    fontSize = 16.sp,
                    color = AppTheme.colors.title,
                    fontWeight = FontWeight.Bold
                ),
                modifier = Modifier
                    .padding(start = 12.dp)
                    .weight(1f),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (device.sn.isNotEmpty()) {
                Image(
                    painter = painterResource(id = R.drawable.ic_delete),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(18.dp)
                        .debouncedClickable(onClick = {
                            onDeleteDevice.invoke(device)
                        })
                )
            } else {
                Image(
                    painter = painterResource(id = R.drawable.ic_plus),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(20.dp)
                )
            }
        }
    }
}
