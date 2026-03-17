package com.ledvance.energy.manager.state

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.connected.system.extensions.openBluetooth
import com.ledvance.energy.manager.dialog.LedvanceDialog
import com.ledvance.ui.R
import com.ledvance.utils.BluetoothManager

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/11 14:30
 * Describe : BluetoothEnableState
 */


@Composable
fun rememberBluetoothEnableState(): EnableState {
    val context = LocalContext.current
    val bluetoothEnable by BluetoothManager.bluetoothEnableState.collectAsStateWithLifecycle()
    var showRequestDialog by rememberSaveable { mutableStateOf(false) }
    if (showRequestDialog) {
        LedvanceDialog(
            title = stringResource(R.string.dialog_bluetooth_enable_request_title),
            message = stringResource(R.string.dialog_bluetooth_enable_request_message),
            cancelText = stringResource(id = R.string.cancel),
            confirmText = stringResource(id = R.string.go_to_setting),
            onCancel = {
                showRequestDialog = false
            },
            onConfirm = {
                showRequestDialog = false
                context.openBluetooth()
            }
        )
    }
    val bluetoothEnableState = remember(bluetoothEnable) {
        BluetoothEnableState(isBluetoothEnable = bluetoothEnable) {
            showRequestDialog = true
        }
    }
    return bluetoothEnableState
}


private class BluetoothEnableState(
    val isBluetoothEnable: Boolean,
    private val showRequestDialog: () -> Unit
) : EnableState {
    override fun hasEnable(autoShowRequestDialog: Boolean): Boolean {
        if (!isBluetoothEnable && autoShowRequestDialog) {
            showRequestDialog.invoke()
        }
        return isBluetoothEnable
    }

    override val value: EnableState
        get() = this

}