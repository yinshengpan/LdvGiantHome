package com.ledvance.ui.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.ledvance.connected.system.extensions.openAppDetail
import com.ledvance.ui.R
import com.ledvance.ui.dialog.LedvanceDialog
import com.ledvance.utils.extensions.BLUETOOTH_PERMISSION

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/11 14:38
 * Describe : BluetoothPermissionState
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberBluetoothPermissionState(): BluetoothPermissionState {
    val context = LocalContext.current.applicationContext
    val permissionState = rememberMultiplePermissionsState(permissions = BLUETOOTH_PERMISSION)
    var showRequestDialog by rememberSaveable { mutableStateOf(false) }
    if (showRequestDialog) {
        LedvanceDialog(
            title = stringResource(R.string.dialog_bluetooth_permissions_request_title),
            message = stringResource(R.string.dialog_bluetooth_permissions_request_message),
            cancelText = stringResource(id = R.string.cancel),
            confirmText = stringResource(id = R.string.go_to_setting),
            onCancel = {
                showRequestDialog = false
            },
            onConfirm = {
                showRequestDialog = false
                context.openAppDetail()
            }
        )
    }
    val multiplePermissionsState = remember(permissionState) {
        MutableBluetoothPermissionState(permissionState) {
            showRequestDialog = true
        }
    }
    return multiplePermissionsState
}

@OptIn(ExperimentalPermissionsApi::class)
private class MutableBluetoothPermissionState(
    private val permissionState: MultiplePermissionsState,
    private val showRequestDialog: () -> Unit
) : BluetoothPermissionState {
    override fun hasGranted(): Boolean {
        return when {
            permissionState.shouldShowRationale -> {
                showRequestDialog.invoke()
                false
            }

            !permissionState.allPermissionsGranted -> {
                permissionState.launchMultiplePermissionRequest()
                false
            }

            else -> true
        }
    }

    override val value: BluetoothPermissionState
        get() = this
}

@Stable
interface BluetoothPermissionState : State<BluetoothPermissionState> {
    fun hasGranted(): Boolean
}