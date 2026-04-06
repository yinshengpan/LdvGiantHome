package com.ledvance.ui.state

import android.Manifest
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
import com.google.accompanist.permissions.PermissionState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import com.ledvance.connected.system.extensions.openAppDetail
import com.ledvance.ui.R
import com.ledvance.ui.dialog.LedvanceDialog

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/03/20 21:00
 * Describe : MicPermissionState
 */

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun rememberMicPermissionState(): MicPermissionState {
    val context = LocalContext.current.applicationContext
    val permissionState = rememberPermissionState(permission = Manifest.permission.RECORD_AUDIO)
    var showRequestDialog by rememberSaveable { mutableStateOf(false) }

    LedvanceDialog(
        visible = showRequestDialog,
        title = stringResource(R.string.dialog_mic_permissions_request_title),
        message = stringResource(R.string.dialog_mic_permissions_request_message),
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

    val micPermissionState = remember(permissionState) {
        MutableMicPermissionState(permissionState) {
            showRequestDialog = true
        }
    }
    return micPermissionState
}

@OptIn(ExperimentalPermissionsApi::class)
private class MutableMicPermissionState(
    private val permissionState: PermissionState,
    private val showRequestDialog: () -> Unit
) : MicPermissionState {
    override fun hasGranted(): Boolean {
        return when {
            permissionState.status.shouldShowRationale -> {
                showRequestDialog.invoke()
                false
            }

            !permissionState.status.isGranted -> {
                permissionState.launchPermissionRequest()
                false
            }

            else -> true
        }
    }

    override val value: MicPermissionState
        get() = this
}

@Stable
interface MicPermissionState : State<MicPermissionState> {
    fun hasGranted(): Boolean
}
