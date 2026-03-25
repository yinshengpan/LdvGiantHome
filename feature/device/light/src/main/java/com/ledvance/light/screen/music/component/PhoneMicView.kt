package com.ledvance.light.screen.music.component

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.ledvance.light.state.rememberMicRecorderState
import com.ledvance.ui.R
import com.ledvance.ui.component.MicSensitivitySlider
import com.ledvance.ui.state.rememberMicPermissionState
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/22 13:29
 * Describe : PhoneMicView
 */
@Composable
internal fun PhoneMicView(
    phoneMicSensitivity: Int,
    onPhoneMicSensitivityChange: (Int) -> Unit,
) {
    val micPermissionState = rememberMicPermissionState()
    var micPermissionGranted by remember { mutableStateOf(false) }

    LifecycleResumeEffect(Unit) {
        micPermissionGranted = micPermissionState.hasGranted()
        onPauseOrDispose { }
    }

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 15.dp)) {
        if (micPermissionGranted) {
            val recorderState = rememberMicRecorderState()
            PhoneMicPulseUI(amplitude = recorderState.amplitude)
            MicSensitivitySlider(
                sensitivity = phoneMicSensitivity,
                modifier = Modifier.padding(vertical = 15.dp),
                onSensitivityChange = {
                    onPhoneMicSensitivityChange.invoke(it)
                    recorderState.sensitivity = it
                },
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.music_phone_mic_permission_required),
                        style = AppTheme.typography.bodyMedium,
                        color = AppTheme.colors.title,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun PhoneMicPulseUI(amplitude: Float) {
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
            text = stringResource(R.string.music_mic_desc),
            style = AppTheme.typography.bodyMedium,
            color = AppTheme.colors.title,
            modifier = Modifier.padding(bottom = 20.dp)
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp),
            contentAlignment = Alignment.Center
        ) {
            val primary = AppTheme.colors.primary
            Canvas(modifier = Modifier.fillMaxSize()) {
                val center = Offset(size.width / 2, size.height / 2)
                val baseRadius = 60.dp.toPx()
                val pulseExtra = animatedAmplitude * 60.dp.toPx()

                // Outer glow rings
                drawCircle(
                    color = Color(0xFFFFBC00).copy(alpha = 0.3f),
                    radius = baseRadius + pulseExtra * 2f,
                    center = center,
                    style = Stroke(width = 2.dp.toPx())
                )
                drawCircle(
                    color = primary.copy(alpha = 0.6f),
                    radius = baseRadius + pulseExtra * 1.2f,
                    center = center,
                    style = Stroke(width = 4.dp.toPx())
                )
                // Inner solid circle
                drawCircle(
                    color = primary,
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