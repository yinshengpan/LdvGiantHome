package com.ledvance.light.state

import android.annotation.SuppressLint
import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.ledvance.light.screen.music.analyzer.AudioLightDispatcher
import com.ledvance.light.screen.music.analyzer.FftProcessor
import com.ledvance.utils.extensions.tryCatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.ln

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : Encapsulated State Holder for Microphone recording logic using AudioRecord for FFT
 */
@Stable
class MicRecorderState(
    private val context: Context,
    sensitivity: Int,
    private val coroutineScope: CoroutineScope
) {
    companion object {
        private const val TAG = "MicRecorderState"
        private const val SAMPLE_RATE = 44100
        private const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        private const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
    }

    var amplitude by mutableFloatStateOf(0f)
        private set

    var sensitivity by mutableIntStateOf(sensitivity)

    var onAudioData: ((r: Int, g: Int, b: Int, brightness: Int) -> Unit)? = null

    private var audioRecord: AudioRecord? = null
    private var job: Job? = null

    private val minBufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT)

    @SuppressLint("MissingPermission")
    fun startRecording() {
        Timber.tag(TAG).d("startRecording called, audioRecord is null: ${audioRecord == null}")
        if (audioRecord != null) return

        try {
            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                minBufferSize.coerceAtLeast(2048)
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                Timber.tag(TAG).e("AudioRecord initialization failed")
                release()
                return
            }

            audioRecord?.startRecording()
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
            val buffer = ShortArray(minBufferSize.coerceAtLeast(2048))
            while (isActive) {
                tryCatch {
                    val readSize = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (readSize > 0) {
                        var sumAmplitude = 0f
                        for (i in 0 until readSize) {
                            sumAmplitude += abs(buffer[i].toInt())
                        }
                        val avgAmplitude = sumAmplitude / readSize
                        val normalized = (avgAmplitude / 32768f).coerceIn(0f, 1f)

                        // ⭐ 灵敏度（0~100）
                        val gain = 0.5f + (sensitivity / 100f) * 2f  // 0.5x ~ 2.5x
                        val boosted = (normalized * gain).coerceIn(0f, 1f)
                        val newAmplitude = ln(1 + boosted * 9) / ln(10f)
                        if (newAmplitude < 0.1) {
                            amplitude = 0f
                            return@tryCatch
                        }
                        amplitude = newAmplitude
                        val fft = FftProcessor.calculateMagnitude(buffer)
                        AudioLightDispatcher.dispatchAudioData(fft, newAmplitude)
                    }
                }
                delay(20)
            }
        }
    }

    fun release() {
        Timber.tag(TAG).d("release called, audioRecord is null: ${audioRecord == null}")
        job?.cancel()
        job = null
        try {
            audioRecord?.stop()
            Timber.tag(TAG).d("AudioRecord stopped")
        } catch (e: Exception) {
            Timber.tag(TAG).w("AudioRecord stop failed: ${e.message}")
        }
        try {
            audioRecord?.release()
            Timber.tag(TAG).d("AudioRecord released")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "AudioRecord release failed")
        }
        audioRecord = null
        amplitude = 0f
        Timber.tag(TAG).i("MicRecorderState resources fully released")
    }
}

@Composable
fun rememberMicRecorderState(sensitivity: Int = 60): MicRecorderState {
    val context = LocalContext.current.applicationContext
    val coroutineScope = rememberCoroutineScope()
    val state = remember { MicRecorderState(context, sensitivity, coroutineScope) }

    LifecycleResumeEffect(state) {
        state.startRecording()
        onPauseOrDispose {
            state.release()
        }
    }
    
    return state
}
