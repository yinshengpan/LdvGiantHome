package com.ledvance.music.state

import android.content.Context
import android.media.audiofx.Visualizer
import android.net.Uri
import androidx.annotation.OptIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.ledvance.domain.bean.MusicItem
import com.ledvance.music.analyzer.AudioLightDispatcher
import com.ledvance.music.effect.LightEffectMode
import com.ledvance.utils.extensions.tryCatch
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.math.sqrt
import kotlin.random.Random

// Persist across tab switches
private var persistentLastPlayedIndex = 0
private var persistentPlaybackMode = PlaybackMode.SEQUENTIAL

internal enum class PlaybackMode {
    SEQUENTIAL, LOOP_ONE, SHUFFLE
}

@OptIn(markerClass = [UnstableApi::class])
@Stable
internal class MusicPlayerState(
    private val context: Context,
    private val coroutineScope: CoroutineScope
) {
    companion object {
        private const val TAG = "MusicPlayerState"
    }

    val musicList = MusicItem.allMusicItems
    
    var currentIndex by mutableIntStateOf(persistentLastPlayedIndex)
        private set
    var isPlaying by mutableStateOf(false)
        private set
    var currentPosition by mutableLongStateOf(0L)
        private set
    var duration by mutableLongStateOf(0L)
        private set
    var playbackMode by mutableStateOf(persistentPlaybackMode)
        private set

    var onAudioData: ((r: Int, g: Int, b: Int, brightness: Int) -> Unit)? = null

    private var exoPlayer: ExoPlayer? = null
    private var progressJob: Job? = null
    private var visualizer: Visualizer? = null

    fun initialize() {
        Timber.tag(TAG).d("MusicPlayerState initialize. exoPlayer is null: ${exoPlayer == null}")
        if (exoPlayer != null) return
        try {
            val player = ExoPlayer.Builder(context).build()
            player.addListener(object : Player.Listener {
                override fun onPlaybackStateChanged(state: Int) {
                    Timber.tag(TAG).v("onPlaybackStateChanged: $state")
                    if (state == Player.STATE_READY) {
                        duration = player.duration.coerceAtLeast(0L)
                        Timber.tag(TAG).d("Player STATE_READY. Duration: $duration ms")
                    } else if (state == Player.STATE_ENDED) {
                        Timber.tag(TAG).i("Playback ended for current track")
                        handlePlaybackEnded()
                    }
                }
                override fun onIsPlayingChanged(playerIsPlaying: Boolean) {
                    isPlaying = playerIsPlaying
                    Timber.tag(TAG).d("onIsPlayingChanged: $playerIsPlaying")
                    if (playerIsPlaying) {
                        startProgressTracking()
                    } else {
                        stopProgressTracking()
                    }
                }
                override fun onAudioSessionIdChanged(audioSessionId: Int) {
                    Timber.tag(TAG).d("onAudioSessionIdChanged: $audioSessionId")
                    if (audioSessionId > 0) {
                        setupVisualizer(audioSessionId)
                    }
                }
            })
            exoPlayer = player
            val sessionId = player.audioSessionId
            if (sessionId > 0) {
                setupVisualizer(sessionId)
            }
            playTrack(currentIndex, playFromZero = true)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to initialize ExoPlayer")
        }
    }

    private fun setupVisualizer(sessionId: Int) {
        try {
            visualizer?.release()
            visualizer = Visualizer(sessionId).apply {
                captureSize = Visualizer.getCaptureSizeRange()[1]
                setDataCaptureListener(object : Visualizer.OnDataCaptureListener {
                    override fun onWaveFormDataCapture(v: Visualizer?, waveform: ByteArray?, samplingRate: Int) {}
                    override fun onFftDataCapture(v: Visualizer?, fft: ByteArray?, samplingRate: Int) {
                        if (exoPlayer?.isPlaying != true) {
                            return
                        }
                        fft?.let { data ->
                            tryCatch {
                                val n = data.size / 2
                                val magnitude = FloatArray(n)
                                for (i in 0 until n) {
                                    val real = data[2 * i].toFloat()
                                    val imag = data[2 * i + 1].toFloat()
                                    // Amplify the magnitudes so they fall in the same range as the Mic PCM records (~1000+)
                                    magnitude[i] = sqrt(real * real + imag * imag) * 20f
                                }
                                // 🎯 振幅（推荐用 RMS）
                                // Calculate un-amplified amplitude to preserve the expected 0..1 scale correctly
                                val amplitude = (magnitude.average().toFloat() / 20f) / 128f
                                AudioLightDispatcher.dispatchAudioData(magnitude, amplitude,LightEffectMode.RELAX)
                            }
                        }
                    }
                }, Visualizer.getMaxCaptureRate() / 2, false, true)
                enabled = true
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to setup Visualizer")
        }
    }

    private fun handlePlaybackEnded() {
        if (musicList.isEmpty()) {
            Timber.tag(TAG).w("handlePlaybackEnded: musicList is empty")
            return
        }
        Timber.tag(TAG).d("handlePlaybackEnded. Mode: $playbackMode")
        when (playbackMode) {
            PlaybackMode.SEQUENTIAL -> {
                val next = (currentIndex + 1) % musicList.size
                playTrack(next, playFromZero = true)
            }
            PlaybackMode.LOOP_ONE -> {
                playTrack(currentIndex, playFromZero = true)
            }
            PlaybackMode.SHUFFLE -> {
                var next = Random.nextInt(musicList.size)
                if (next == currentIndex && musicList.size > 1) {
                    next = (next + 1) % musicList.size
                }
                playTrack(next, playFromZero = true)
            }
        }
    }

    fun playTrack(index: Int, playFromZero: Boolean = false) {
        if (musicList.isEmpty()) {
            Timber.tag(TAG).e("playTrack failed: musicList is empty")
            return
        }
        if (exoPlayer == null) {
            Timber.tag(TAG).e("playTrack failed: exoPlayer is null")
            return
        }
        try {
            currentIndex = index
            persistentLastPlayedIndex = index
            
            val item = musicList[index]
            val uri = Uri.parse("asset:///${item.fileName}")
            val mediaItem = MediaItem.fromUri(uri)
            Timber.tag(TAG).i("Playing track $index: ${item.title} (${item.fileName})")
            
            exoPlayer?.setMediaItem(mediaItem)
            exoPlayer?.prepare()
            if (playFromZero) {
                exoPlayer?.seekTo(0)
            }
            exoPlayer?.play()
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Error playing track at index $index")
        }
    }

    fun togglePlayPause() {
        Timber.tag(TAG).d("togglePlayPause. current isPlaying: $isPlaying")
        try {
            if (isPlaying) {
                exoPlayer?.pause()
            } else {
                exoPlayer?.play()
            }
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "togglePlayPause failed")
        }
    }

    fun playNext() {
        if (musicList.isEmpty()) return
        val nextIndex = (currentIndex + 1) % musicList.size
        playTrack(nextIndex, playFromZero = true)
    }

    fun playPrevious() {
        if (musicList.isEmpty()) return
        val prevIndex = (currentIndex - 1 + musicList.size) % musicList.size
        playTrack(prevIndex, playFromZero = true)
    }

    fun togglePlaybackMode() {
        val oldMode = playbackMode
        playbackMode = when (playbackMode) {
            PlaybackMode.SEQUENTIAL -> PlaybackMode.LOOP_ONE
            PlaybackMode.LOOP_ONE -> PlaybackMode.SHUFFLE
            PlaybackMode.SHUFFLE -> PlaybackMode.SEQUENTIAL
        }
        Timber.tag(TAG).i("Playback mode changed: $oldMode -> $playbackMode")
        persistentPlaybackMode = playbackMode
    }

    fun seekTo(position: Long) {
        try {
            exoPlayer?.seekTo(position)
            currentPosition = position
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "seekTo failed")
        }
    }

    private fun startProgressTracking() {
        progressJob?.cancel()
        progressJob = coroutineScope.launch {
            while (true) {
                currentPosition = exoPlayer?.currentPosition ?: 0L
                delay(100)
            }
        }
    }

    private fun stopProgressTracking() {
        progressJob?.cancel()
    }

    fun release() {
        Timber.tag(TAG).d("MusicPlayerState release called")
        persistentLastPlayedIndex = currentIndex
        persistentPlaybackMode = playbackMode
        stopProgressTracking()
        visualizer?.enabled = false
        visualizer?.release()
        visualizer = null
        try {
            exoPlayer?.stop()
            Timber.tag(TAG).d("Player stopped")
        } catch (e: Exception) {
            Timber.tag(TAG).w("Player stop failed: ${e.message}")
        }
        try {
            exoPlayer?.release()
            Timber.tag(TAG).d("Player released")
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Player release failed")
        }
        exoPlayer = null
        Timber.tag(TAG).i("MusicPlayerState released")
    }
}

/**
 * Creates and remembers a [MusicPlayerState] hooked to the Compose lifecycle
 * to guarantee flawless memory leak prevention.
 */
@Composable
internal fun rememberMusicPlayerState(): MusicPlayerState {
    val context = LocalContext.current.applicationContext
    val coroutineScope = rememberCoroutineScope()
    val state = remember { MusicPlayerState(context, coroutineScope) }

    DisposableEffect(state) {
        state.initialize()
        onDispose {
            state.release()
        }
    }
    
    return state
}
