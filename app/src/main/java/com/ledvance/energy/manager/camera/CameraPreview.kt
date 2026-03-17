package com.ledvance.energy.manager.camera

import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.ledvance.utils.extensions.tryCatch

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/14/25 11:01
 * Describe : CameraPreview
 */
@Composable
fun CameraPreview(onCamera: (Camera?) -> Unit = {}, onScan: (String) -> Unit = {}) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember {
        ProcessCameraProvider.getInstance(context)
    }
    AndroidView(factory = { ctx ->
        var camera: Camera? = null
        val previewView = PreviewView(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(MATCH_PARENT, MATCH_PARENT)
        }
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }
            val analyser = BarCodeAnalyser.get(ctx, object : BarCodeAnalyser.BarCodeCallback {
                override fun onZoom(zoom: Float): Boolean {
                    tryCatch {
                        val cameraControl = camera?.cameraControl ?: return false
                        cameraControl.setZoomRatio(zoom)
                    }
                    return true
                }

                override fun onQRScanned(value: String) {
                    onScan.invoke(value)
                }
            })
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    analyser
                )
                onCamera.invoke(camera)
            } catch (e: Exception) {
                onCamera.invoke(null)
            }
        }, ContextCompat.getMainExecutor(ctx))
        previewView
    }, modifier = Modifier.fillMaxSize())
}