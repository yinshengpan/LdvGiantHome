package com.ledvance.light.state

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.exoplayer.ExoPlayer
import com.ledvance.domain.bean.MusicItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import kotlin.random.Random

// Persist across tab switches
private var persistentLastPlayedIndex = 0
private var persistentPlaybackMode = PlaybackMode.SEQUENTIAL

enum class PlaybackMode {
    SEQUENTIAL, LOOP_ONE, SHUFFLE
}

@Stable
class MusicPlayerState(
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

    private var exoPlayer: ExoPlayer? = null
    private var progressJob: Job? = null

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
            })
            exoPlayer = player
            playTrack(currentIndex, playFromZero = true)
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "Failed to initialize ExoPlayer")
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
fun rememberMusicPlayerState(): MusicPlayerState {
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
