package com.ledvance.light.component

import android.Manifest
import android.media.MediaRecorder
import android.os.Build
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.ledvance.domain.bean.command.DeviceMic
import com.ledvance.light.bean.MusicSegment
import com.ledvance.ui.component.LedvanceRadioGroup
import com.ledvance.ui.component.MicSensitivitySlider
import com.ledvance.ui.state.rememberMicPermissionState
import com.ledvance.ui.theme.AppTheme
import com.ledvance.ui.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 17:50
 * Describe : MusicModeControl
 */
@Composable
fun MusicModeControl() {

    val allMusicSegment = remember { MusicSegment.allMusicSegment }
    var selectedMusicSegment by remember {
        mutableStateOf(MusicSegment.DeviceMic)
    }

    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.screenBackground),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(paddingValues = PaddingValues(vertical = 20.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(
                text = "Music",
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.title,
                modifier = Modifier.fillMaxWidth()
            )

            LedvanceRadioGroup(
                selectorItem = selectedMusicSegment,
                items = allMusicSegment,
                modifier = Modifier.padding(top = 15.dp, bottom = 10.dp),
                shape = RoundedCornerShape(8.dp),
                checkedColor = Color.White,
                backgroundColor = AppTheme.colors.divider,
                checkedTextColor = AppTheme.colors.title,
                textColor = AppTheme.colors.title,
                onCheckedChange = {
                    if (it is MusicSegment) {
                        selectedMusicSegment = it
                    }
                }
            )
            when (selectedMusicSegment) {
                MusicSegment.DeviceMic -> DeviceMic()
                MusicSegment.PhoneMic -> PhoneMic()
                MusicSegment.Music -> MusicControl()
            }
        }
    }
}

@Composable
private fun DeviceMic() {
    val rhythmList = remember { DeviceMic.items }
    Text(
        text = "Transform lighting effects according to music rhythm",
        style = AppTheme.typography.bodyMedium,
        color = AppTheme.colors.title,
        modifier = Modifier.padding(bottom = 15.dp)
    )
    FlowRow(
        maxItemsInEachRow = 2,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        rhythmList.forEach {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp)
                    .background(
                        color = AppTheme.colors.screenSecondaryBackground,
                        shape = RoundedCornerShape(4.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = it.title,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.title,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
    MicSensitivitySlider(
        sensitivity = 100,
        modifier = Modifier.padding(top = 15.dp),
        onSensitivityChange = {

        },
    )
}

@Composable
private fun PhoneMic() {
    val context = LocalContext.current
    val micPermissionState = rememberMicPermissionState()
    var micPermissionGranted by remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        micPermissionGranted = micPermissionState.hasGranted()
        onPauseOrDispose { }
    }

    if (micPermissionGranted) {
        val recorderState = rememberMicRecorderState()
        PhoneMicPulseUI(amplitude = recorderState.amplitude)
    } else {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "Microphone permission is required.",
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.title,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
fun PhoneMicPulseUI(amplitude: Float) {
    val animatedAmplitude by animateFloatAsState(
        targetValue = amplitude,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "amplitudeAnimation"
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Transform lighting effects according to music rhythm",
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.title,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val baseRadius = 60.dp.toPx()
                val pulseExtra = animatedAmplitude * 30.dp.toPx()
                
                // Outer glow rings
                drawCircle(
                    color = Color(0xFF673AB7).copy(alpha = 0.5f),
                    radius = baseRadius + pulseExtra * 2f,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = Color(0xFFE91E63).copy(alpha = 0.8f),
                    radius = baseRadius + pulseExtra * 1.2f,
                    center = center,
                    style = Stroke(width = 4.dp.toPx())
                )
                // Inner solid circle
                drawCircle(
                    color = Color(0xFF5E35B1),
                    radius = baseRadius + pulseExtra * 0.5f,
                    center = center
                )
            }

            Icon(
                painter = painterResource(id = R.drawable.ic_mic),
                contentDescription = "Mic",
                tint = Color.White,
                modifier = Modifier.size(64.dp)
            )
        }
    }
}