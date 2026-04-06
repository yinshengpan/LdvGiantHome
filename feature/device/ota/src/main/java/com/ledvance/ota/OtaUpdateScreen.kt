package com.ledvance.ota

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.domain.bean.DeviceId
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.dialog.LedvanceDialog
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:53
 * Describe : OtaUpdateScreen
 */
@Composable
internal fun OtaUpdateScreen(
    deviceId: DeviceId,
    viewModel: OtaUpdateContract = hiltViewModel<OtaUpdateViewModel, OtaUpdateViewModel.Factory>(creationCallback = {
        it.create(deviceId)
    }),
    onBack: () -> Unit,
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    var showUpdateDialog by remember { mutableStateOf(false) }
    var showLearnDialog by remember { mutableStateOf(false) }

    LedvanceDialog(
        visible = showUpdateDialog,
        title = stringResource(R.string.dialog_firmware_upgrade_title),
        message = stringResource(R.string.dialog_firmware_upgrade_message),
        cancelText = stringResource(R.string.cancel),
        confirmText = stringResource(R.string.confirm),
        onCancel = {
            showUpdateDialog = false
        },
        onConfirm = {
            showUpdateDialog = false
            viewModel.startUpdateFirmware()
        },
    )

    LedvanceDialog(
        visible = showLearnDialog,
        title = stringResource(R.string.dialog_firmware_upgrade_learn_title),
        message = stringResource(R.string.dialog_firmware_upgrade_learn_message),
        confirmText = stringResource(R.string.got_it),
        confirmTextColor = AppTheme.colors.dialogPositive,
        cancelText = null,
        onConfirm = {
            showLearnDialog = false
        },
    )

    LedvanceScreen(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        onBackPressed = {
            if (viewModel.isOtaUpdating()) {
                showLearnDialog = true
                return@LedvanceScreen
            }
            onBack.invoke()
        },
        title = stringResource(R.string.title_firmware_update),
    ) {
        when (uiState) {
            OtaUpdateContract.UiState.Loading -> {}
            is OtaUpdateContract.UiState.Success -> {
                OtaUpdateScreenContent(
                    uiState = uiState as OtaUpdateContract.UiState.Success,
                    startUpdateFirmware = { tryAgain ->
                        if (tryAgain) {
                            viewModel.startUpdateFirmware()
                            return@OtaUpdateScreenContent
                        }
                        showUpdateDialog = true
                    },
                )
            }
        }
    }
}