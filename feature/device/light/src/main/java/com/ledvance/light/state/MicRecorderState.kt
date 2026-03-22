package com.ledvance.light.state

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.lifecycle.compose.LifecycleResumeEffect
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
import timber.log.Timber
import kotlin.math.sqrt

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
    companion object {
        private const val TAG = "MicRecorderState"
    }

    var amplitude by mutableFloatStateOf(0f)
        private set

    private var recorder: MediaRecorder? = null
    private var job: Job? = null

    fun startRecording() {
        Timber.tag(TAG).d("startRecording called, recorder is null: ${recorder == null}")
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
            Timber.tag(TAG).i("Microphone recording started successfully")
            startPolling()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to start microphone recording")
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
                // Using sqrt to boost lower volume levels (more sensitive).
                val normalized = (maxAmp / 32767f).coerceIn(0f, 1f)
                val boosted = sqrt(normalized.toDouble()).toFloat()
                if (maxAmp > 0) {
                    Timber.tag(TAG).v("Polling amplitude: maxAmp=$maxAmp, normalized=$normalized, boosted=$boosted")
                }
                withContext(Dispatchers.Main) {
                    amplitude = boosted
                }
                delay(50) // poll roughly 20 times a second
            }
        }
    }

    fun release() {
        Timber.tag(TAG).d("release called, recorder is null: ${recorder == null}")
        job?.cancel()
        job = null
        try {
            recorder?.stop()
            Timber.tag(TAG).d("Recorder stopped")
        } catch (e: Exception) {
            Timber.tag(TAG).w("Recorder stop failed: ${e.message}")
        }
        try {
            recorder?.release()
            Timber.tag(TAG).d("Recorder released")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Recorder release failed")
        }
        recorder = null
        amplitude = 0f
        Timber.tag(TAG).i("MicRecorderState resources fully released")
    }
}

@Composable
fun rememberMicRecorderState(): MicRecorderState {
    val context = LocalContext.current.applicationContext
    val coroutineScope = rememberCoroutineScope()
    val state = remember { MicRecorderState(context, coroutineScope) }

    LifecycleResumeEffect(state) {
        state.startRecording()
        onPauseOrDispose {
            state.release()
        }
    }
    
    return state
}
