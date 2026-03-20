package com.ledvance.light.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ledvance.light.bean.MusicSegment
import com.ledvance.ui.component.IRadioGroupItem
import com.ledvance.ui.component.LedvanceRadioGroup
import com.ledvance.ui.component.MicSensitivitySlider
import com.ledvance.ui.theme.AppTheme

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
        mutableStateOf<IRadioGroupItem<String>>(MusicSegment.DeviceMic)
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
                    selectedMusicSegment = it
                }
            )
        }
    }
}

@Composable
private fun DeviceMic() {
    Column() {
        Text(text = "Transform lighting effects according to music rhythm")

        MicSensitivitySlider(
            sensitivity = 100,
            modifier = Modifier.padding(top = 10.dp),
            onSensitivityChange = {

            },
        )
    }
}