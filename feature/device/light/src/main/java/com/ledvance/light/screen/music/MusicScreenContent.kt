package com.ledvance.light.screen.music

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ledvance.domain.bean.command.DeviceMicRhythm
import com.ledvance.light.bean.MusicSegment
import com.ledvance.light.screen.music.component.DeviceMicView
import com.ledvance.light.screen.music.component.MusicPlayView
import com.ledvance.light.screen.music.component.PhoneMicView
import com.ledvance.ui.component.LedvanceRadioGroup
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 17:50
 * Describe : MusicScreenContent
 */
@Composable
internal fun MusicScreenContent(
    uiState: MusicContract.UiState.Success,
    onSensitivityChange: (Int) -> Unit,
    onRhythmChange: (DeviceMicRhythm) -> Unit,
    onMusicSegmentChange: (MusicSegment) -> Unit,
) {

    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.screenBackground),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(paddingValues = PaddingValues(20.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 15.dp)
        ) {
            LedvanceRadioGroup(
                selectorItem = uiState.musicSegment,
                items = uiState.musicSegmentList,
                modifier = Modifier.padding(bottom = 10.dp, start = 15.dp, end = 15.dp),
                shape = RoundedCornerShape(8.dp),
                checkedColor = Color.White,
                backgroundColor = AppTheme.colors.divider,
                checkedTextColor = AppTheme.colors.title,
                textColor = AppTheme.colors.title,
                onCheckedChange = {
                    if (it is MusicSegment) {
                        onMusicSegmentChange(it)
                    }
                }
            )
            when (uiState.musicSegment) {
                MusicSegment.DeviceMic -> {
                    DeviceMicView(
                        sensitivity = uiState.deviceMicSensitivity,
                        selectedRhythm = uiState.deviceMicRhythm,
                        rhythmList = uiState.deviceMicRhythmList,
                        onSensitivityChange = onSensitivityChange,
                        onRhythmChange = onRhythmChange,
                    )
                }

                MusicSegment.PhoneMic -> {
                    PhoneMicView(
                        sensitivity = uiState.deviceMicSensitivity,
                        onSensitivityChange = onSensitivityChange,
                    )
                }

                MusicSegment.Music -> {
                    MusicPlayView()
                }
            }
        }
    }
}