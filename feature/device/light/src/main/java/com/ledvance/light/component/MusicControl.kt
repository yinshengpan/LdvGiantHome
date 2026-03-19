package com.ledvance.light.component

import android.media.MediaPlayer
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.domain.bean.MusicItem
import com.ledvance.ui.theme.AppTheme
import com.ledvance.ui.R
import kotlinx.coroutines.delay

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 18:16
 * Describe : MusicControl
 */
@Composable
fun MusicControl() {
    val context = LocalContext.current
    val musicList = remember { MusicItem.allMusicItems }
    var currentItem by remember { mutableStateOf(musicList.first()) }
    var isPlaying by remember { mutableStateOf(false) }
    var currentPosition by remember { mutableLongStateOf(0L) }
    var duration by remember { mutableLongStateOf(0L) }
    var isRepeat by remember { mutableStateOf(false) }

    val mediaPlayer = remember { MediaPlayer() }

    fun playMusic(item: MusicItem) {
        try {
            mediaPlayer.reset()
            val resId = context.resources.getIdentifier("m${item.id}", "raw", context.packageName)
            if (resId == 0) return 
            val afd = context.resources.openRawResourceFd(resId)
            mediaPlayer.setDataSource(afd.fileDescriptor, afd.startOffset, afd.length)
            afd.close()
            mediaPlayer.prepare()
            mediaPlayer.start()
            isPlaying = true
            duration = mediaPlayer.duration.toLong()
            currentItem = item
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    DisposableEffect(Unit) {
        playMusic(currentItem)
        onDispose {
            mediaPlayer.stop()
            mediaPlayer.release()
        }
    }

    // Update progress
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            currentPosition = mediaPlayer.currentPosition.toLong()
            delay(500)
        }
    }

    mediaPlayer.setOnCompletionListener {
        if (isRepeat) {
            mediaPlayer.start()
        } else {
            val nextIndex = (musicList.indexOf(currentItem) + 1) % musicList.size
            playMusic(musicList[nextIndex])
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.colors.screenBackground)
    ) {
        // Playlist
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            items(musicList) { item ->
                val isSelected = item == currentItem
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(if (isSelected) Color(0xFFBC00FF) else Color.Transparent)
                        .clickable { playMusic(item) }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.id.toString(),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.width(40.dp)
                    )
                    Column {
                        Text(
                            text = item.title,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Normal
                        )
                        Text(
                            text = item.subtitle,
                            color = Color.White.copy(alpha = 0.6f),
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }

        // Playback Controls
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF3F3F3F))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Timer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = formatTime(currentPosition), color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                    Text(text = formatTime(duration), color = Color.White.copy(alpha = 0.7f), fontSize = 14.sp)
                }

                // Seek bar
                Slider(
                    value = currentPosition.toFloat(),
                    onValueChange = {
                        mediaPlayer.seekTo(it.toInt())
                        currentPosition = it.toLong()
                    },
                    valueRange = 0f..duration.toFloat().coerceAtLeast(1f),
                    colors = SliderDefaults.colors(
                        thumbColor = Color(0xFFBC00FF),
                        activeTrackColor = Color(0xFFBC00FF),
                        inactiveTrackColor = Color.Gray
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Transport Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        val prevIndex = (musicList.indexOf(currentItem) - 1 + musicList.size) % musicList.size
                        playMusic(musicList[prevIndex])
                    }) {
                        Icon(painter = painterResource(R.drawable.ic_add), contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    FloatingActionButton(
                        onClick = {
                            if (isPlaying) {
                                mediaPlayer.pause()
                            } else {
                                mediaPlayer.start()
                            }
                            isPlaying = !isPlaying
                        },
                        containerColor = Color.White.copy(alpha = 0.2f),
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(
                            if (isPlaying) painterResource(android.R.drawable.ic_media_pause) else painterResource(R.drawable.ic_add),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    IconButton(onClick = {
                        val nextIndex = (musicList.indexOf(currentItem) + 1) % musicList.size
                        playMusic(musicList[nextIndex])
                    }) {
                        Icon(painter = painterResource(R.drawable.ic_add), contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }

                    IconButton(onClick = { isRepeat = !isRepeat }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_add),
                            contentDescription = null,
                            tint = if (isRepeat) Color(0xFFBC00FF) else Color.White
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