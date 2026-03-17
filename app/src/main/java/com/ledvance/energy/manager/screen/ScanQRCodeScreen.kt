package com.ledvance.energy.manager.screen

import androidx.camera.core.Camera
import androidx.camera.core.TorchState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.energy.manager.camera.CameraPreview
import com.ledvance.energy.manager.dialog.LedvanceDialog
import com.ledvance.energy.manager.viewmodel.ScanQRCodeViewModel
import com.ledvance.ui.R
import com.ledvance.ui.extensions.debouncedClickable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/14/25 10:36
 * Describe : ScanQRCodeScreen
 */
@Composable
fun ScanQRCodeScreen(
    onBack: () -> Unit,
    viewModel: ScanQRCodeViewModel = hiltViewModel(),
    onResult: (String) -> Unit
) {
    var camera by remember { mutableStateOf<Camera?>(null) }
    var showCameraPreview by remember { mutableStateOf(true) }
    var isTorchOn by remember(camera) {
        mutableStateOf(camera?.cameraInfo?.torchState?.value == TorchState.ON)
    }
    val scanSN by viewModel.getScanSNFlow().collectAsStateWithLifecycle()
    LaunchedEffect(scanSN) {
        if (scanSN.isNotEmpty()) {
            showCameraPreview = false
            onResult.invoke(scanSN)
        }
    }
    val showErrorDialog by viewModel.getInvalidQRCodeDialogFlow().collectAsStateWithLifecycle()
    if (showErrorDialog) {
        LedvanceDialog(
            title = "Wrong QR Code Detected",
            message = "This QR code is not the product SN. Please scan the SN label printed on the box.",
            confirmText = stringResource(R.string.got_it),
            onConfirm = {
                viewModel.hideInvalidQRCodeDialog()
            },
            cancelText = null
        )
    }
    Box(modifier = Modifier.fillMaxSize()) {
        if (showCameraPreview) {
            CameraPreview(
                onCamera = {
                    camera = it
                },
                onScan = {
                    viewModel.handleResult(it)
                },
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .height(48.dp)
                .padding(horizontal = 20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_back),
                contentDescription = null,
                modifier = Modifier
                    .size(24.dp)
                    .debouncedClickable(onClick = {
                        showCameraPreview = false
                        onBack.invoke()
                    }),
                tint = Color.White
            )
            Icon(
                painter = painterResource(if (isTorchOn) R.drawable.ic_flash_on else R.drawable.ic_flash_off),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 15.dp)
                    .size(24.dp)
                    .debouncedClickable(onClick = {
                        camera?.cameraControl?.enableTorch(!isTorchOn)
                        isTorchOn = !isTorchOn
                    }),
                tint = Color.White
            )
        }
    }
}