package com.ledvance.music.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ledvance.domain.bean.command.DeviceMicRhythm
import com.ledvance.ui.R
import com.ledvance.ui.component.MicSensitivitySlider
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.extensions.getIconResId
import com.ledvance.ui.extensions.getNameResId
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/22 13:22
 * Describe : DeviceMicView
 */
@Composable
internal fun DeviceMicView(
    deviceMicSensitivity: Int,
    selectedRhythm: DeviceMicRhythm,
    rhythmList: List<DeviceMicRhythm>,
    onDeviceMicSensitivityChange: (Int) -> Unit,
    onRhythmChange: (DeviceMicRhythm) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
    ) {
        Text(
            text = stringResource(R.string.music_mic_desc),
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
                RhythmItem(
                    rhythm = it,
                    isSelected = it == selectedRhythm,
                    onRhythmChange = onRhythmChange
                )
            }
        }
        MicSensitivitySlider(
            sensitivity = deviceMicSensitivity,
            modifier = Modifier.padding(vertical = 15.dp),
            onSensitivityChange = onDeviceMicSensitivityChange,
        )
    }
}

@Composable
private fun FlowRowScope.RhythmItem(rhythm: DeviceMicRhythm, isSelected: Boolean, onRhythmChange: (DeviceMicRhythm) -> Unit) {
    Row(
        modifier = Modifier
            .weight(1f)
            .height(48.dp)
            .background(
                color = AppTheme.colors.screenSecondaryBackground,
                shape = RoundedCornerShape(4.dp)
            )
            .then(
                other = if (isSelected) Modifier.border(
                    width = 2.dp,
                    color = AppTheme.colors.primary,
                    shape = RoundedCornerShape(4.dp)
                ) else Modifier
            )
            .padding(horizontal = 10.dp)
            .debouncedClickable(
                onClick = {
                    onRhythmChange.invoke(rhythm)
                },
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Text(
            text = stringResource(rhythm.getNameResId()),
            style = AppTheme.typography.bodyMedium,
            color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.title,
            textAlign = TextAlign.Start,
            modifier = Modifier.weight(1f)
        )

        Image(
            painter = painterResource(rhythm.getIconResId()),
            contentDescription = null,
            modifier = Modifier.size(36.dp)
        )
    }
}
