package com.ledvance.energy.manager.camera

import android.content.Context
import androidx.camera.core.ImageAnalysis
import androidx.core.content.ContextCompat
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/14/25 11:02
 * Describe : BarCodeAnalyser
 */
object BarCodeAnalyser {

    interface BarCodeCallback {
        fun onZoom(zoom: Float): Boolean
        fun onQRScanned(value: String)
    }

    fun get(ctx: Context, callback: BarCodeCallback): ImageAnalysis {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .setZoomSuggestionOptions(
                ZoomSuggestionOptions.Builder { zoom -> callback.onZoom(zoom) }
                    .setMaxSupportedZoomRatio(3f)
                    .build()
            )
            .build()
        val scanner = BarcodeScanning.getClient(options)
        val imageAnalyzer = ImageAnalysis.Builder()
            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
            .build()
            .also { analyzer ->
                analyzer.setAnalyzer(ContextCompat.getMainExecutor(ctx)) { imageProxy ->
                    val mediaImage = imageProxy.image
                    if (mediaImage != null) {
                        val image = InputImage.fromMediaImage(
                            mediaImage,
                            imageProxy.imageInfo.rotationDegrees
                        )
                        scanner.process(image)
                            .addOnSuccessListener {
                                it?.forEach { barcode ->
                                    barcode.rawValue?.let {
                                        callback.onQRScanned(it)
                                    }
                                }
                            }
                            .addOnCompleteListener {
                                imageProxy.close()
                            }
                    }
                }
            }
        return imageAnalyzer
    }
}