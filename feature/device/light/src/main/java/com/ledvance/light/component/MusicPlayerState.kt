package com.ledvance.light.component

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
        if (exoPlayer != null) return
        val player = ExoPlayer.Builder(context).build()
        player.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(state: Int) {
                if (state == Player.STATE_READY) {
                    duration = player.duration.coerceAtLeast(0L)
                } else if (state == Player.STATE_ENDED) {
                    handlePlaybackEnded()
                }
            }
            override fun onIsPlayingChanged(playerIsPlaying: Boolean) {
                isPlaying = playerIsPlaying
                if (playerIsPlaying) {
                    startProgressTracking()
                } else {
                    stopProgressTracking()
                }
            }
        })
        exoPlayer = player
        playTrack(currentIndex, playFromZero = true)
    }

    private fun handlePlaybackEnded() {
        if (musicList.isEmpty()) return
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
        if (musicList.isEmpty() || exoPlayer == null) return
        currentIndex = index
        persistentLastPlayedIndex = index
        
        val item = musicList[index]
        val uri = Uri.parse("asset:///${item.fileName}")
        val mediaItem = MediaItem.fromUri(uri)
        
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
        if (playFromZero) {
            exoPlayer?.seekTo(0)
        }
        exoPlayer?.play()
    }

    fun togglePlayPause() {
        if (isPlaying) {
            exoPlayer?.pause()
        } else {
            exoPlayer?.play()
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
        playbackMode = when (playbackMode) {
            PlaybackMode.SEQUENTIAL -> PlaybackMode.LOOP_ONE
            PlaybackMode.LOOP_ONE -> PlaybackMode.SHUFFLE
            PlaybackMode.SHUFFLE -> PlaybackMode.SEQUENTIAL
        }
        persistentPlaybackMode = playbackMode
    }

    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
        currentPosition = position
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
        persistentLastPlayedIndex = currentIndex
        persistentPlaybackMode = playbackMode
        stopProgressTracking()
        exoPlayer?.stop()
        exoPlayer?.release()
        exoPlayer = null
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
