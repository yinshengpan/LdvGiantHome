package com.ledvance.light.screen.music.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.domain.bean.MusicItem
import com.ledvance.light.state.PlaybackMode
import com.ledvance.light.state.rememberMusicPlayerState
import com.ledvance.ui.R
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme

@Composable
internal fun MusicPlayView() {
    val playerState = rememberMusicPlayerState()
    Column(
        modifier = Modifier
            .fillMaxSize()
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        ) {
            itemsIndexed(playerState.musicList) { index, item ->
                val isSelected = playerState.currentIndex == index
                MusicItemView(
                    item = item,
                    isSelected = isSelected,
                    onItemClick = {
                        playerState.playTrack(index, playFromZero = true)
                    }
                )
                if (index < playerState.musicList.size - 1) {
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 24.dp),
                        thickness = 1.dp,
                        color = AppTheme.colors.divider
                    )
                }
            }
        }
        PlayControlView(
            isPlaying = playerState.isPlaying,
            currentPosition = playerState.currentPosition,
            duration = playerState.duration,
            playbackMode = playerState.playbackMode,
            togglePlayPause = { playerState.togglePlayPause() },
            playPrevious = { playerState.playPrevious() },
            playNext = { playerState.playNext() },
            seekTo = { playerState.seekTo(it) },
            togglePlaybackMode = { playerState.togglePlaybackMode() }
        )
    }
}

@Composable
private fun PlayControlView(
    isPlaying: Boolean,
    currentPosition: Long,
    duration: Long,
    playbackMode: PlaybackMode,
    modifier: Modifier = Modifier,
    togglePlayPause: () -> Unit,
    playPrevious: () -> Unit,
    playNext: () -> Unit,
    seekTo: (Long) -> Unit,
    togglePlaybackMode: () -> Unit,
) {
    Column(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .background(Color(0xFFF5F5F5)) // Light grey background to distinguish from white
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            // Seek bar & Timer
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = formatTime(currentPosition),
                    color = AppTheme.colors.title.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.width(12.dp))
                // Custom Progress Bar to avoid the "broken/gap" issue in M3 LinearProgressIndicator
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(6.dp)
                        .background(Color(0xFFE0E0E0), RoundedCornerShape(3.dp))
                        .clip(RoundedCornerShape(3.dp))
                ) {
                    val progress = (currentPosition.toFloat() / duration.toFloat().coerceAtLeast(1f)).coerceIn(0f, 1f)
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(progress)
                            .background(Color(0xFFFF6600))
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = formatTime(duration),
                    color = AppTheme.colors.title.copy(alpha = 0.6f),
                    fontSize = 12.sp
                )
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Centered Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.weight(1f))
                // Previous
                IconButton(onClick = playPrevious, modifier = Modifier.weight(1f)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_play_next),
                        contentDescription = "Previous",
                        tint = AppTheme.colors.title,
                        modifier = Modifier
                            .size(28.dp)
                            .graphicsLayer(scaleX = -1f)
                    )
                }

                // Play/Pause
                Box(
                    modifier = Modifier
                        .padding(horizontal = 15.dp)
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFFF6600))
                        .clickable(onClick = togglePlayPause),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = if (isPlaying) painterResource(R.drawable.ic_pause) else painterResource(R.drawable.ic_play),
                        contentDescription = "Play/Pause",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // Next
                IconButton(onClick = playNext, modifier = Modifier.weight(1f)) {
                    Icon(
                        painter = painterResource(R.drawable.ic_play_next),
                        contentDescription = "Next",
                        tint = AppTheme.colors.title,
                        modifier = Modifier.size(28.dp)
                    )
                }
                // Play Mode
                IconButton(
                    onClick = togglePlaybackMode,
                    modifier = Modifier.weight(1f)
                ) {
                    val modeIcon = when (playbackMode) {
                        PlaybackMode.SEQUENTIAL -> R.drawable.ic_play_mode_order
                        PlaybackMode.LOOP_ONE -> R.drawable.ic_play_mode_loop
                        PlaybackMode.SHUFFLE -> R.drawable.ic_play_mode_shuffle
                    }
                    Icon(
                        painter = painterResource(modeIcon),
                        contentDescription = "Play Mode",
                        tint = AppTheme.colors.title,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MusicItemView(
    item: MusicItem,
    isSelected: Boolean,
    onItemClick: (MusicItem) -> Unit
) {
    val contentColor = if (isSelected) Color(0xFFFF6600) else AppTheme.colors.title
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .debouncedClickable(onClick = { onItemClick.invoke(item) })
            .padding(horizontal = 24.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = item.id.toString(),
            color = contentColor,
            fontSize = 16.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            modifier = Modifier.width(24.dp)
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = contentColor,
                fontSize = 16.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
            Text(
                text = item.subtitle,
                color = contentColor.copy(alpha = 0.5f),
                fontSize = 12.sp
            )
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val minStr = if (minutes < 10) "0$minutes" else "$minutes"
    val secStr = if (seconds < 10) "0$seconds" else "$seconds"
    return "$minStr:$secStr"
}