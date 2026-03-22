package com.ledvance.light.screen.music.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ledvance.domain.bean.command.DeviceMicRhythm
import com.ledvance.ui.component.MicSensitivitySlider
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/22 13:22
 * Describe : DeviceMicView
 */
@Composable
internal fun DeviceMicView(
    sensitivity: Int,
    selectedRhythm: DeviceMicRhythm,
    rhythmList: List<DeviceMicRhythm>,
    onSensitivityChange: (Int) -> Unit,
    onRhythmChange: (DeviceMicRhythm) -> Unit,
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 15.dp),
    ) {
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
                        )
                        .then(
                            other = if (selectedRhythm == it) Modifier.border(
                                width = 1.dp,
                                color = AppTheme.colors.primary,
                                shape = RoundedCornerShape(4.dp)
                            ) else Modifier
                        )
                        .debouncedClickable(
                            onClick = {
                                onRhythmChange.invoke(it)
                            },
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
            sensitivity = sensitivity,
            modifier = Modifier.padding(vertical = 15.dp),
            onSensitivityChange = onSensitivityChange,
        )
    }
}