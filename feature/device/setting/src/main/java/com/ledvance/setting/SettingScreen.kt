package com.ledvance.setting

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.rememberModalBottomSheetState
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
import com.ledvance.ui.component.OfflineBanner
import com.ledvance.ui.dialog.LedvanceDialog
import com.ledvance.ui.theme.AppTheme
import com.ledvance.ui.utils.OneTimeActionEffect

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:53
 * Describe : SettingScreen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SettingScreen(
    deviceId: DeviceId,
    viewModel: SettingContract = hiltViewModel<SettingViewModel, SettingViewModel.Factory>(creationCallback = {
        it.create(deviceId = deviceId)
    }),
    onBackClick: () -> Unit,
    onDeleteSuccess: () -> Unit,
    onNavigateToOta: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showLineSequencePicker by remember { mutableStateOf(false) }
    var showResetDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    viewModel.OneTimeActionEffect {
        when (it) {
            SettingContract.SettingOneTimeAction.DeleteSuccess -> {
                onDeleteSuccess()
            }
        }
    }

    LedvanceScreen(
        title = stringResource(R.string.title_setting),
        onBackPressed = onBackClick,
        isLoading = (uiState as? SettingContract.UiState.Success)?.loading ?: false
    ) {
        when (uiState) {
            SettingContract.UiState.Error -> {}
            SettingContract.UiState.Loading -> {}
            is SettingContract.UiState.Success -> {
                val state = uiState as SettingContract.UiState.Success
                SettingScreenContent(
                    uiState = state,
                    onLineSequenceClick = { showLineSequencePicker = true },
                    onResetClick = { showResetDialog = true },
                    onUpgradeClick = onNavigateToOta,
                    onDeleteClick = { showDeleteDialog = true }
                )

                OfflineBanner(
                    visible = !state.isOnline,
                    onReconnectClick = { viewModel.onReconnect() },
                )
            }
        }
    }

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    if (showLineSequencePicker) {
        val successState = uiState as? SettingContract.UiState.Success
        ModalBottomSheet(
            onDismissRequest = { showLineSequencePicker = false },
            sheetState = sheetState,
            sheetGesturesEnabled = false,
            dragHandle = null
        ) {
            LineSequencePicker(
                initialSequence = successState?.lineSequence,
                onCancel = { showLineSequencePicker = false },
                onConfirm = {
                    viewModel.setLineSequence(it)
                    showLineSequencePicker = false
                }
            )
        }
    }

    if (showResetDialog) {
        LedvanceDialog(
            title = stringResource(R.string.dialog_reset_title),
            message = stringResource(R.string.dialog_reset_message),
            onCancel = { showResetDialog = false },
            onConfirm = {
                viewModel.resetDevice()
                showResetDialog = false
            }
        )
    }

    if (showDeleteDialog) {
        LedvanceDialog(
            title = stringResource(R.string.dialog_delete_device_title),
            message = stringResource(R.string.dialog_delete_device_message),
            onCancel = { showDeleteDialog = false },
            onConfirm = {
                viewModel.deleteDevice()
                showDeleteDialog = false
            }
        )
    }
}