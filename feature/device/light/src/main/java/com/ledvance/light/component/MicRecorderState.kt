package com.ledvance.light.component

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : Encapsulated State Holder for Microphone recording logic
 */
@Stable
class MicRecorderState(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    var amplitude by mutableFloatStateOf(0f)
        private set

    private var recorder: MediaRecorder? = null
    private var job: Job? = null

    fun startRecording() {
        if (recorder != null) return
        
        recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            MediaRecorder(context)
        } else {
            @Suppress("DEPRECATION")
            MediaRecorder()
        }

        try {
            recorder?.setAudioSource(MediaRecorder.AudioSource.MIC)
            recorder?.setOutputFormat(MediaRecorder.OutputFormat.AMR_NB)
            recorder?.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
            recorder?.setOutputFile("/dev/null")
            recorder?.prepare()
            recorder?.start()
            startPolling()
        } catch (e: Exception) {
            e.printStackTrace()
            release()
        }
    }

    private fun startPolling() {
        job?.cancel()
        job = coroutineScope.launch(Dispatchers.Default) {
            while (isActive) {
                val maxAmp = try { 
                    recorder?.maxAmplitude?.toFloat() ?: 0f 
                } catch (e: Exception) { 
                    0f 
                }
                // maxAmplitude is up to 32767. 
                val normalized = (maxAmp / 32767f).coerceIn(0f, 1f)
                withContext(Dispatchers.Main) {
                    amplitude = normalized
                }
                delay(50) // poll roughly 20 times a second
            }
        }
    }

    fun release() {
        job?.cancel()
        job = null
        try {
            recorder?.stop()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        try {
            recorder?.release()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        recorder = null
        amplitude = 0f
    }
}

@Composable
fun rememberMicRecorderState(): MicRecorderState {
    val context = LocalContext.current.applicationContext
    val coroutineScope = rememberCoroutineScope()
    val state = remember { MicRecorderState(context, coroutineScope) }

    DisposableEffect(state) {
        state.startRecording()
        onDispose {
            state.release()
        }
    }
    
    return state
}
