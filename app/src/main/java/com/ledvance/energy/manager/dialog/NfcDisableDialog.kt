package com.ledvance.energy.manager.dialog

import android.content.Intent
import android.provider.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.ledvance.ui.R
import com.ledvance.utils.extensions.tryCatch

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/12 17:00
 * Describe : NfcDisableDialog
 */
@Composable
fun NfcDisableDialog(onCancel: () -> Unit = {}) {
    val context = LocalContext.current
    LedvanceDialog(
        title = stringResource(R.string.nfc_disable_dialog_title),
        message = stringResource(R.string.nfc_disable_dialog_content),
        confirmText = stringResource(R.string.go_to_setting),
        cancelText = stringResource(R.string.cancel),
        onCancel = onCancel,
        onConfirm = {
            tryCatch {
                context.startActivity(Intent(Settings.ACTION_NFC_SETTINGS))
            }
        }
    )
}