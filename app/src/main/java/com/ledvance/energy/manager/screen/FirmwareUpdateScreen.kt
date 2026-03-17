package com.ledvance.energy.manager.screen

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.energy.manager.dialog.LedvanceDialog
import com.ledvance.energy.manager.viewmodel.FirmwareUpdateState
import com.ledvance.energy.manager.viewmodel.FirmwareUpdateViewModel
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/13/25 14:51
 * Describe : FirmwareUpdateScreen
 */
@Composable
fun FirmwareUpdateScreen(
    onBack: (isBackToDeviceList: Boolean) -> Unit,
    viewModel: FirmwareUpdateViewModel = hiltViewModel()
) {
    val firmwareUIState by viewModel.getFirmwareUIStateFlow().collectAsStateWithLifecycle()
    var showUpdateDialog by remember { mutableStateOf(false) }
    var showLearnDialog by remember { mutableStateOf(false) }
    if (showUpdateDialog) {
        LedvanceDialog(
            title = stringResource(R.string.firmware_update_dialog_title),
            message = stringResource(R.string.firmware_update_dialog_content),
            cancelText = stringResource(R.string.cancel),
            confirmText = stringResource(R.string.update),
            onCancel = {
                showUpdateDialog = false
            },
            onConfirm = {
                showUpdateDialog = false
                viewModel.startUpdateFirmware()
            },
        )
    }

    if (showLearnDialog) {
        LedvanceDialog(
            title = stringResource(R.string.firmware_updating_learn_dialog_title),
            message = stringResource(R.string.firmware_updating_learn_dialog_content),
            confirmText = stringResource(R.string.got_it),
            confirmTextColor = AppTheme.colors.dialogPositive,
            cancelText = null,
            onConfirm = {
                showLearnDialog = false
            },
        )
    }

    BackHandler {
        if (viewModel.isUpdating()) {
            showLearnDialog = true
            return@BackHandler
        }
        onBack.invoke(viewModel.isUpdateSuccess())
    }
    LedvanceScreen(title = stringResource(R.string.update_firmware), onBackPressed = {
        if (viewModel.isUpdating()) {
            showLearnDialog = true
            return@LedvanceScreen
        }
        onBack.invoke(viewModel.isUpdateSuccess())
    }) {
        when (firmwareUIState) {
            is FirmwareUpdateState.UpdateProgress -> {
                FirmwareUpdateProgress(firmwareUIState as FirmwareUpdateState.UpdateProgress)
            }

            is FirmwareUpdateState.FirmwareInfo -> {
                FirmwareUpdateAvailable(firmwareUIState as FirmwareUpdateState.FirmwareInfo) {
                    showUpdateDialog = true
                }
            }

            is FirmwareUpdateState.NoUpdateAvailable -> {
                FirmwareNoUpdateAvailable(firmwareUIState as FirmwareUpdateState.NoUpdateAvailable)
            }

            FirmwareUpdateState.UpdateFailed -> {
                ErrorView(onTryAgain = {
                    viewModel.startUpdateFirmware()
                })
            }

            else -> {}
        }
    }
}

@Composable
private fun FirmwareUpdateProgress(updateProgress: FirmwareUpdateState.UpdateProgress) {
    var progress by remember { mutableFloatStateOf(0f) }
    LaunchedEffect(updateProgress.progress) {
        progress = updateProgress.progress * 1f / 100
    }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                progress = { animatedProgress },
                modifier = Modifier.size(105.dp),
                strokeWidth = 5.dp,
                gapSize = 0.dp,
                trackColor = AppTheme.colors.progressBackground,
                strokeCap = StrokeCap.Square,
                color = AppTheme.colors.progress
            )
            Text(
                text = "${updateProgress.progress}%",
                style = AppTheme.typography.titleMedium.copy(fontSize = 16.sp),
                color = AppTheme.colors.title,
                modifier = Modifier.padding(start = 3.dp)
            )
        }
        Text(
            text = stringResource(R.string.firmware_updating_tips),
            color = AppTheme.colors.title,
            style = AppTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(top = 30.dp, start = 20.dp, end = 20.dp)
        )
    }

}

@Composable
private fun FirmwareNoUpdateAvailable(state: FirmwareUpdateState.NoUpdateAvailable) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Image(
            painter = painterResource(R.drawable.ic_success),
            contentScale = ContentScale.FillWidth,
            contentDescription = null,
            modifier = Modifier.size(111.dp)
        )
        Text(
            text = stringResource(R.string.no_updates_available),
            style = AppTheme.typography.titleMedium.copy(fontSize = 15.sp),
            color = AppTheme.colors.title,
            modifier = Modifier.padding(top = 20.dp)
        )
        Text(
            text = "${stringResource(R.string.current_version)}:${state.curVersion}",
            style = AppTheme.typography.bodyMedium.copy(fontSize = 14.sp),
            color = AppTheme.colors.title,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}

@Composable
private fun FirmwareUpdateAvailable(
    firmwareInfo: FirmwareUpdateState.FirmwareInfo, onClickUpdate: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(20.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White, contentColor = Color.White
        ),
        shape = RoundedCornerShape(7.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
        ) {

            Row(
                modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f), verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "${stringResource(R.string.update_found)}:${firmwareInfo.newVersion}",
                        style = AppTheme.typography.titleMedium.copy(fontSize = 15.sp),
                        color = AppTheme.colors.title,
                    )
                    Text(
                        text = firmwareInfo.newOtaFileSize,
                        style = AppTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                        color = AppTheme.colors.body,
                        modifier = Modifier.padding(top = 5.dp)
                    )
                }
                Text(
                    text = stringResource(R.string.update),
                    style = AppTheme.typography.titleSmall,
                    color = AppTheme.colors.buttonContent,
                    modifier = Modifier
                        .background(
                            color = AppTheme.colors.buttonBackground,
                            shape = RoundedCornerShape(4.dp)
                        )
                        .debouncedClickable {
                            onClickUpdate.invoke()
                        }
                        .padding(horizontal = 8.dp, vertical = 8.dp))
            }
            HorizontalDivider(
                color = AppTheme.colors.divider, modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "${stringResource(R.string.current_version)}:${firmwareInfo.curVersion}",
                style = AppTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = AppTheme.colors.body,
                modifier = Modifier.padding(top = 10.dp)
            )
            Text(
                text = "${stringResource(R.string.new_version)}:${firmwareInfo.newVersion}",
                style = AppTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                color = AppTheme.colors.body,
                modifier = Modifier.padding(top = 10.dp)
            )
        }
    }
}

@Composable
private fun ErrorView(onTryAgain: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(top = 50.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(R.drawable.ic_fr_update_failed),
            contentDescription = null,
            modifier = Modifier.padding(horizontal = 46.dp).size(111.dp),
            contentScale = ContentScale.FillWidth
        )
        Text(
            text = stringResource(R.string.update_failed),
            modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 30.dp),
            style = AppTheme.typography.labelMedium,
            color = Color.Red
        )
        Text(
            text = stringResource(id = R.string.try_again),
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp, top = 30.dp)
                .debouncedClickable() {
                    onTryAgain.invoke()
                },
            style = AppTheme.typography.bodyMedium.copy(color = AppTheme.colors.primary)
        )
    }
}