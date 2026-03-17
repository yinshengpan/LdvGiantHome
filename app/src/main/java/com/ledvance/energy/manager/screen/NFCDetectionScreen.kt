package com.ledvance.energy.manager.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.rememberAsyncImagePainter
import com.ledvance.energy.manager.dialog.LedvanceDialog
import com.ledvance.energy.manager.dialog.NfcDisableDialog
import com.ledvance.energy.manager.dialog.NfcNotSupportDialog
import com.ledvance.energy.manager.viewmodel.NFCViewModel
import com.ledvance.nfc.utils.NfcProgressState
import com.ledvance.nfc.utils.isValidCrc8
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.theme.AppTheme
import com.ledvance.utils.DeviceManager
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/20/25 11:28
 * Describe : NFCDetectionScreen
 */
private const val TAG = "NFCDetectionScreen"
@Composable
fun NFCDetectionScreen(
    viewModel: NFCViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onResult: (String) -> Unit
) {
    var showInvalidSNDialog by remember { mutableStateOf(false) }
    var closeAllDialog by remember { mutableStateOf(false) }
    val driverModel by viewModel.driverModel.collectAsStateWithLifecycle()
    val readingProgress by viewModel.readingProgress.collectAsStateWithLifecycle()
    LaunchedEffect(driverModel) {
        Timber.tag(TAG).d("driverModel->$driverModel")
        val deviceSN = driverModel?.evChargerParam?.sn ?: return@LaunchedEffect
        if (DeviceManager.isValidSN(deviceSN) && driverModel?.isValidCrc8() == true) {
            onResult.invoke(deviceSN)
        } else {
            showInvalidSNDialog = true
        }
    }
    LaunchedEffect(readingProgress) {
        Timber.tag(TAG).d("readingProgress->$readingProgress")
    }

    if (readingProgress == NfcProgressState.Fail || showInvalidSNDialog) {
        LedvanceDialog(
            title = stringResource(R.string.unrecognized_device_dialog_title),
            message = stringResource(R.string.unrecognized_device_dialog_content),
            confirmText = stringResource(R.string.got_it),
            onConfirm = {
                viewModel.reset()
                showInvalidSNDialog = false
            },
            cancelText = null
        )
    }
    val hasSupportedNfc = remember { viewModel.hasSupportNfc() }
    val nfcEnable by viewModel.nfcEnable.collectAsStateWithLifecycle()
    if (!nfcEnable && hasSupportedNfc && !closeAllDialog) {
        NfcDisableDialog(onCancel = {
            closeAllDialog = true
            onBack.invoke()
        })
    }
    if (!hasSupportedNfc && !closeAllDialog) {
        NfcNotSupportDialog(onConfirm = {
            closeAllDialog = true
            onBack.invoke()
        })
    }
    DisposableEffect(Unit) {
        viewModel.enableNfcReader()
        onDispose {
            viewModel.disableNfcReaderOrWriter()
        }
    }

    LedvanceScreen(title = stringResource(R.string.nfc_detection), onBackPressed = onBack) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 78.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Image(
                painter = rememberAsyncImagePainter(R.mipmap.gif_close_to_tag),
                contentDescription = null,
                modifier = Modifier
                    .fillMaxWidth(),
                contentScale = ContentScale.FillWidth
            )
            Text(
                text = stringResource(R.string.nfc_connect_tips),
                modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 30.dp),
                style = AppTheme.typography.bodyMedium,
                color = AppTheme.colors.body,
                textAlign = TextAlign.Center
            )
        }
    }
}