package com.ledvance.ui.state

import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.connected.system.extensions.openLocation
import com.ledvance.ui.R
import com.ledvance.ui.dialog.LedvanceDialog
import com.ledvance.utils.LocationManager

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/11 13:16
 * Describe : LocationEnableState
 */
@Composable
fun rememberLocationEnableState(): EnableState {
    val context = LocalContext.current
    val locationEnable by LocationManager.locationEnable.collectAsStateWithLifecycle()
    var showRequestDialog by rememberSaveable { mutableStateOf(false) }
    if (showRequestDialog) {
        LedvanceDialog(
            title = stringResource(id = R.string.dialog_location_enable_request_title),
            message = stringResource(id = R.string.dialog_location_enable_request_message),
            cancelText = stringResource(id = R.string.cancel),
            confirmText = stringResource(id = R.string.go_to_setting),
            onCancel = {
                showRequestDialog = false
            },
            onConfirm = {
                showRequestDialog = false
                context.openLocation()
            }
        )
    }
    val locationEnableState = remember(locationEnable) {
        LocationEnableState(isLocationEnable = locationEnable) {
            showRequestDialog = true
        }
    }
    return locationEnableState
}

private class LocationEnableState(
    val isLocationEnable: Boolean,
    private val showRequestDialog: () -> Unit
) : EnableState {
    override fun hasEnable(autoShowRequestDialog: Boolean): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            return true
        }
        if (!isLocationEnable && autoShowRequestDialog) {
            showRequestDialog.invoke()
        }
        return isLocationEnable
    }

    override val value: EnableState
        get() = this

}