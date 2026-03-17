package com.ledvance.energy.manager.dialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ledvance.ui.R

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/20 09:51
 * Describe : NfcNotSupportDialog
 */
@Composable
fun NfcNotSupportDialog(onConfirm:()-> Unit) {
    LedvanceDialog(
        title = stringResource(R.string.nfc_not_support_dialog_title),
        message = stringResource(R.string.nfc_not_support_dialog_content),
        confirmText = stringResource(R.string.got_it),
        cancelText = null,
        onConfirm = onConfirm
    )
}