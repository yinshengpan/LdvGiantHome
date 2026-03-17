package com.ledvance.energy.manager.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.Lifecycle.Event.ON_PAUSE
import androidx.lifecycle.Lifecycle.Event.ON_RESUME
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.ledvance.ble.bean.ConnectStatus
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.ble.constant.Constants
import com.ledvance.database.model.ChargerEntity
import com.ledvance.database.model.DeviceEntity
import com.ledvance.energy.manager.dialog.LedvanceDialog
import com.ledvance.energy.manager.navigation.FirmwareUpdateRoute
import com.ledvance.energy.manager.navigation.NavigationRoute
import com.ledvance.energy.manager.viewmodel.BleViewModel
import com.ledvance.energy.manager.viewmodel.DeviceDetailFactory
import com.ledvance.energy.manager.viewmodel.DeviceDetailViewModel
import com.ledvance.ui.R
import com.ledvance.ui.component.InputTextType
import com.ledvance.ui.component.LedvanceButton
import com.ledvance.ui.component.LedvanceInputText
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.component.LoadingCard
import com.ledvance.ui.component.rememberSnackBarState
import com.ledvance.ui.component.showToast
import com.ledvance.ui.extensions.ComposableLifecycle
import com.ledvance.ui.extensions.InitializeScope
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.extensions.stringResourceFormat
import com.ledvance.ui.theme.AppTheme
import com.ledvance.utils.extensions.getString
import kotlinx.coroutines.launch
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/5/27 14:50
 * Describe : DeviceDetailScreen
 */
private const val TAG = "DeviceDetailScreen"

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DeviceDetailScreen(
    device: ScannedDevice,
    bleViewModel: BleViewModel = hiltViewModel(),
    viewModel: DeviceDetailViewModel = hiltViewModel<DeviceDetailViewModel, DeviceDetailFactory> {
        it.create(device.address)
    },
    onBack: () -> Unit,
    onGotoPage: (NavigationRoute) -> Unit,
) {
    val scope = rememberCoroutineScope()
    var loading by remember { mutableStateOf(false) }
    val snackBarState = rememberSnackBarState()
    val connectStatus by bleViewModel.getConnectStatusFlow().collectAsStateWithLifecycle()
    val deviceOnline by bleViewModel.getDeviceOnlineFlow().collectAsStateWithLifecycle()
    val addChargerRestarting by bleViewModel.addChargerRestarting.collectAsStateWithLifecycle()
    val localDevice by viewModel.device.collectAsStateWithLifecycle()
    val chargerList by viewModel.chargerList.collectAsStateWithLifecycle()

    var tripCurrent by remember { mutableStateOf("") }

    LaunchedEffect(connectStatus, localDevice) {
        Timber.tag(TAG).i("connectStatus->$connectStatus localDevice->$localDevice")
    }

    InitializeScope {
        bleViewModel.connectDevice(device)
    }

    if (loading) {
        LoadingCard()
    }

    var showInvalidParamValueDialog by remember { mutableStateOf(false) }

    if (showInvalidParamValueDialog) {
        val range = Constants.tripCurrentRange
        LedvanceDialog(
            title = stringResource(R.string.invalid_param_dialog_title),
            message = stringResourceFormat(
                R.string.invalid_param_dialog_content, range.first, range.last
            ),
            confirmText = stringResource(R.string.got_it),
            cancelText = null,
            onConfirm = {
                showInvalidParamValueDialog = false
            })
    }

    ComposableLifecycle {
        when (it) {
            ON_RESUME -> bleViewModel.setCanPolling(true)
            ON_PAUSE -> bleViewModel.setCanPolling(false)
            else -> {}
        }
    }

    LedvanceScreen(
        horizontalAlignment = Alignment.CenterHorizontally,
        title = stringResource(R.string.connected_device),
        actionIconPainter = painterResource(R.drawable.ic_upgrade).takeIf {
            connectStatus == ConnectStatus.Completed
        },
        onActionPressed = {
            onGotoPage.invoke(FirmwareUpdateRoute)
        },
        onBackPressed = {
            onBack.invoke()
        }.takeIf { connectStatus == ConnectStatus.Completed || connectStatus == ConnectStatus.Failed }) {
        when (connectStatus) {
            ConnectStatus.Completed -> {
                localDevice?.also {
                    Column(modifier = Modifier.fillMaxSize()) {
                        DeviceDetailContent(
                            setTripCurrent = tripCurrent,
                            modifier = Modifier.weight(1f),
                            device = it,
                            chargerList = chargerList,
                            addChargerRestarting = addChargerRestarting,
                            onOperationCharger = {
                                scope.launch {
                                    loading = true
                                    val success = bleViewModel.operationCharger(it)
                                    loading = false
                                    val messageResId = if (success) {
                                        R.string.operation_successfully
                                    } else {
                                        R.string.operation_failed
                                    }
                                    snackBarState.showToast(getString(messageResId))
                                }
                            },
                            onTripCurrentChange = {
                                tripCurrent = it
                            },
                            onSendTripCurrent = {
                                scope.launch {
                                    val current = tripCurrent.toIntOrNull() ?: 0
                                    if (current !in Constants.tripCurrentRange) {
                                        showInvalidParamValueDialog = true
                                        return@launch
                                    }
                                    loading = true
                                    val success = bleViewModel.setTripCurrent(it)
                                    loading = false
                                    val messageResId = if (success) {
                                        R.string.operation_successfully
                                    } else {
                                        R.string.operation_failed
                                    }
                                    if (success) {
                                        tripCurrent = ""
                                    }
                                    snackBarState.showToast(getString(messageResId))
                                }
                            },
                        )
                    }

                    if (!deviceOnline) {
                        Text(
                            text = buildAnnotatedString {
                                append("The device is offline. You can’t change settings right now.")
                                val openLink = LinkAnnotation.Clickable(
                                    tag = "Reconnect", styles = TextLinkStyles(
                                        style = SpanStyle(
                                            color = Color(0xFF0D6EFD),
                                            textDecoration = TextDecoration.Underline
                                        )
                                    )
                                ) {
                                    bleViewModel.connectDevice(device)
                                }
                                withLink(openLink) {
                                    append("\t")
                                    append("Reconnect")
                                }
                            },
                            modifier = Modifier
                                .background(Color(0xFFFFF3CD).copy(alpha = 0.95f))
                                .padding(horizontal = 24.dp, vertical = 6.dp)
                                .fillMaxWidth(),
                            style = AppTheme.typography.bodyMedium,
                            color = Color(0xFF856404)
                        )
                    }
                }
            }

            ConnectStatus.Failed -> {
                ErrorView(onTryAgain = {
                    bleViewModel.connectDevice(device)
                })
            }

            else -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(200.dp), color = AppTheme.colors.primary
                    )
                }
            }
        }
    }
}

@Composable
fun NFCProgramView(onProgram: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val rotate by animateFloatAsState(
        targetValue = if (expanded) 0f else 180f,
        animationSpec = tween(durationMillis = 100),
        label = ""
    )
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White, contentColor = Color.White
        ),
        shape = RoundedCornerShape(0.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()

        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 15.dp)
                    .clickable {
                        expanded = !expanded
                    }) {
                Image(
                    painter = painterResource(R.drawable.ic_nfc),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(end = 10.dp)
                        .size(20.dp)
                )
                Text(
                    text = "Set Trip Current by NFC",
                    style = AppTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    color = AppTheme.colors.title,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.weight(1f)
                )

                Icon(
                    painter = painterResource(R.drawable.ic_expand),
                    contentDescription = "Expandable Arrow",
                    modifier = Modifier
                        .size(14.dp)
                        .rotate(rotate),
                    tint = AppTheme.colors.primary
                )
            }
            if (expanded) {
                Box(
                    modifier = Modifier.background(Color(0xFFF4F4F4))
                ) {
                    LedvanceButton(
                        text = "Program",
                        modifier = Modifier
                            .padding(horizontal = 30.dp)
                            .padding(top = 32.dp, bottom = 22.dp)
                    ) {
                        onProgram.invoke()
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceDetailContent(
    setTripCurrent: String,
    device: DeviceEntity,
    chargerList: List<ChargerEntity>,
    addChargerRestarting: String?,
    modifier: Modifier = Modifier.fillMaxSize(),
    onOperationCharger: (ChargerEntity) -> Unit,
    onTripCurrentChange: (String) -> Unit,
    onSendTripCurrent: (String) -> Unit,
) {
    val (pairedChargerList, availableChargerList) = remember(chargerList) {
        chargerList.partition { it.isPaired }
    }

    var showChargerOperationDialog by remember { mutableStateOf<ChargerEntity?>(null) }
    if (showChargerOperationDialog != null) {
        val isPair = showChargerOperationDialog?.isPaired == false
        LedvanceDialog(
            title = stringResource(if (isPair) R.string.pair_charger_dialog_title else R.string.remove_paired_charger_dialog_title),
            message = stringResource(if (isPair) R.string.pair_charger_dialog_content else R.string.remove_paired_charger_dialog_content),
            confirmText = stringResource(if (isPair) R.string.pair else R.string.remove),
            cancelText = stringResource(R.string.cancel),
            onCancel = {
                showChargerOperationDialog = null
            },
            onConfirm = {
                showChargerOperationDialog?.also { onOperationCharger.invoke(it) }
                showChargerOperationDialog = null
            })
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState())
    ) {
        ItemTitle(title = stringResource(R.string.connected_device))
        ItemContent(
            title = stringResource(R.string.energy_controller),
            content = device.sn,
            modifier = Modifier.padding(bottom = 5.dp)
        )
        ItemTitle(title = stringResource(R.string.current_setting))
        Card(
            modifier = Modifier
                .padding(horizontal = 20.dp, vertical = 5.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White, contentColor = Color.White
            ),
            shape = RoundedCornerShape(7.dp),
        ) {
            LedvanceInputText(
                title = stringResource(R.string.set_trip_current),
                value = setTripCurrent,
                unit = "A",
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 13.dp),
                max = Constants.tripCurrentRange.last,
                min = Constants.tripCurrentRange.first,
                inputTextType = InputTextType.Integer,
                onSend = onSendTripCurrent,
                onValueChange = {
                    onTripCurrentChange.invoke(it)
                })
        }
        ItemContent(
            title = "The Current Trip Current Value",
            content = "${device.tripCurrent}",
            unit = "A",
            modifier = Modifier.padding(top = 5.dp)
        )
        ItemContent(
            title = stringResource(R.string.l1),
            content = "${device.l1}",
            unit = "A",
            modifier = Modifier.padding(top = 5.dp)
        )
        ItemContent(
            title = stringResource(R.string.l2),
            content = "${device.l2}",
            unit = "A",
            modifier = Modifier.padding(top = 5.dp)
        )
        ItemTitle(title = stringResource(R.string.charger_list))
        if (pairedChargerList.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 5.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White, contentColor = Color.White
                ),
                shape = RoundedCornerShape(7.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.paired_charger_list),
                        style = AppTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        color = AppTheme.colors.title,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 15.dp, bottom = 10.dp)
                    )
                    HorizontalDivider(color = AppTheme.colors.divider, thickness = 0.5.dp)
                    pairedChargerList.forEach {
                        ChargerItem(it, addChargerRestarting) { showChargerOperationDialog = it }
                    }
                }
            }
        }
        if (availableChargerList.isNotEmpty()) {
            Card(
                modifier = Modifier
                    .padding(horizontal = 20.dp, vertical = 10.dp)
                    .fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color.White, contentColor = Color.White
                ),
                shape = RoundedCornerShape(7.dp),
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(R.string.available_charger_list),
                        style = AppTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        color = AppTheme.colors.title,
                        modifier = Modifier
                            .padding(horizontal = 20.dp)
                            .padding(top = 15.dp, bottom = 10.dp)
                    )
                    HorizontalDivider(color = AppTheme.colors.divider, thickness = 0.5.dp)
                    availableChargerList.forEach {
                        ChargerItem(it) { showChargerOperationDialog = it }
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(50.dp))
    }
}

@Composable
private fun ChargerItem(
    charger: ChargerEntity,
    addChargerRestarting: String? = null,
    onItemClick: (ChargerEntity) -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
    ) {
        Text(
            text = charger.chargerNumber,
            style = AppTheme.typography.titleSmall.copy(
                fontSize = 14.sp,
                color = if (charger.isOnline) AppTheme.colors.title else AppTheme.colors.body
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        if (charger.isPaired && charger.isOnline) {
            Text(
                text = "${charger.chargeCurrent}A",
                style = AppTheme.typography.bodyMedium.copy(fontSize = 16.sp),
                color = AppTheme.colors.title,
                modifier = Modifier.padding(start = 36.dp)
            )
        }
        Spacer(modifier = Modifier.weight(1f))
        if (charger.isOnline || !charger.isPaired) {
            Image(
                painter = painterResource(id = if (charger.isPaired) R.drawable.ic_minus else R.drawable.ic_plus),
                contentDescription = null,
                modifier = Modifier
                    .size(20.dp)
                    .debouncedClickable(onClick = {
                        onItemClick.invoke(charger)
                    })
            )
        } else if (addChargerRestarting == charger.chargerNumber) {
            Text(
                text = "Processing",
                color = Color(0xFF2979FF),
                style = AppTheme.typography.bodyMedium.copy(fontSize = 16.sp)
            )
        }
    }
}

@Composable
private fun ItemTitle(
    title: String,
    showDivider: Boolean = false,
    expandIconPainter: Painter = painterResource(id = R.drawable.ic_expand_less),
    expanded: Boolean? = null,
    onExpanded: (Boolean) -> Unit = {}
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (expanded != null) Modifier.clickable {
                    onExpanded.invoke(!expanded)
                } else Modifier)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = title,
                style = AppTheme.typography.titleMedium.copy(fontSize = 15.sp),
                color = AppTheme.colors.primary,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f)
            )

            expanded?.also {
                val rotate by animateFloatAsState(
                    targetValue = if (expanded) 0f else 180f,
                    animationSpec = tween(durationMillis = 100),
                    label = ""
                )
                Icon(
                    painter = expandIconPainter,
                    contentDescription = "Expandable Arrow",
                    modifier = Modifier
                        .size(24.dp)
                        .rotate(rotate),
                    tint = AppTheme.colors.primary
                )
            }
        }
        if (showDivider) {
            HorizontalDivider(color = AppTheme.colors.divider, thickness = 0.5.dp)
        }
    }
}

@Composable
private fun ItemContent(
    title: String,
    content: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    showArrow: Boolean = false,
    onItemClick: (() -> Unit)? = null
) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 5.dp)
            .then(modifier)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White, contentColor = Color.White
        ),
        shape = RoundedCornerShape(7.dp),
    ) {
        Row(
            modifier = Modifier
                .background(color = Color.White)
                .fillMaxWidth()
                .then(if (onItemClick != null) Modifier.clickable { onItemClick.invoke() } else Modifier)
                .padding(horizontal = 20.dp, vertical = 20.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = title,
                style = AppTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = AppTheme.colors.title,
                modifier = Modifier.wrapContentWidth(Alignment.Start)
            )
            Text(
                text = content,
                style = AppTheme.typography.titleMedium.copy(fontSize = 15.sp),
                color = AppTheme.colors.title,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
            )
            if (!unit.isNullOrEmpty()) {
                Text(
                    text = unit,
                    style = AppTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = AppTheme.colors.title,
                )
            }
            if (showArrow) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_arrow_right),
                    contentDescription = "Arrow",
                    modifier = Modifier.size(14.dp),
                    tint = AppTheme.colors.title
                )
            }
        }
    }
}

@Composable
private fun ErrorView(onTryAgain: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.mipmap.pic_connect_failed),
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 70.dp),
            contentScale = ContentScale.FillWidth
        )
        Text(
            text = stringResource(R.string.connect_failed),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 30.dp),
            style = AppTheme.typography.labelMedium,
            color = Color.Red
        )
        Text(
            text = stringResource(id = R.string.try_again),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 10.dp)
                .debouncedClickable() {
                    onTryAgain.invoke()
                },
            style = AppTheme.typography.bodyMedium.copy(color = AppTheme.colors.primary)
        )
    }
}





