package com.ledvance.light.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.ui.R

@Composable
fun MusicControl() {
    val playerState = rememberMusicPlayerState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF2E2E2E)) // Match screenshot dark theme
    ) {
        playerState.musicList.forEachIndexed { index, item ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { playerState.playTrack(index, playFromZero = true) }
                    .padding(horizontal = 24.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.id.toString(),
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Normal,
                    modifier = Modifier.width(32.dp)
                )
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = item.title,
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Normal
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = item.subtitle,
                        color = Color.White.copy(alpha = 0.5f),
                        fontSize = 14.sp
                    )
                }
            }
        }

        // Playback Controls
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(bottomStart = 10.dp, bottomEnd = 10.dp))
                .background(Color(0xFF6B6B6B)) // Lighter grey background for control panel
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Seek bar & Timer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = formatTime(playerState.currentPosition), color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Slider(
                        value = playerState.currentPosition.toFloat(),
                        onValueChange = {
                            playerState.seekTo(it.toLong())
                        },
                        valueRange = 0f..playerState.duration.toFloat().coerceAtLeast(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = Color(0xFFE040FB), // Bright purple matching screenshot
                            activeTrackColor = Color(0xFFE040FB),
                            inactiveTrackColor = Color(0xFF4A4A4A)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .height(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = formatTime(playerState.duration), color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Transport Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous (ic_play_next horizontally flipped)
                    IconButton(onClick = {
                        playerState.playPrevious()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_play_next),
                            contentDescription = "Previous",
                            tint = Color.White,
                            modifier = Modifier
                                .size(28.dp)
                                .graphicsLayer(scaleX = -1f) // Flip horizontally
                        )
                    }

                    // Play/Pause
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.2f))
                            .clickable {
                                playerState.togglePlayPause()
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = if (playerState.isPlaying) painterResource(R.drawable.ic_pause) else painterResource(R.drawable.ic_play),
                            contentDescription = "Play/Pause",
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // Next
                    IconButton(onClick = {
                        playerState.playNext()
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_play_next),
                            contentDescription = "Next",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    // Play Mode
                    IconButton(onClick = {
                        playerState.togglePlaybackMode()
                    }) {
                        val modeIcon = when (playerState.playbackMode) {
                            PlaybackMode.SEQUENTIAL -> R.drawable.ic_play_mode_order
                            PlaybackMode.LOOP_ONE -> R.drawable.ic_play_mode_loop
                            PlaybackMode.SHUFFLE -> R.drawable.ic_play_mode_shuffle
                        }
                        Icon(
                            painter = painterResource(modeIcon),
                            contentDescription = "Play Mode",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }
            }
        }
    }
}

private fun formatTime(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return String.format("%02d:%02d", minutes, seconds)
}