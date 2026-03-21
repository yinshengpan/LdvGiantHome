package com.ledvance.light.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.domain.bean.command.ModeId
import com.ledvance.light.bean.ModeSegment
import com.ledvance.ui.component.LedvanceRadioGroup
import com.ledvance.ui.component.WheelPicker
import com.ledvance.ui.theme.AppTheme
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 18:16
 * Describe : ModeControl
 */
@Composable
fun ModeControl(
    selectedModeId: ModeId?,
    onModeChange: (ModeId) -> Unit
) {
    val allSegments = remember { ModeSegment.allModeSegment }
    var selectedSegment by remember {
        mutableStateOf(ModeSegment.Base)
    }

    val modes by remember(selectedSegment) {
        derivedStateOf { ModeSegment.getModesBySegment(selectedSegment) }
    }

    val initialIndex by remember(modes) {
        val index = modes.indexOfFirst { it == selectedModeId }
        mutableIntStateOf(if (index != -1) index else 0)
    }

    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.screenBackground),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            Text(
                text = "Mode",
                style = AppTheme.typography.titleMedium,
                color = AppTheme.colors.title,
                modifier = Modifier.fillMaxWidth()
            )

            LedvanceRadioGroup(
                selectorItem = selectedSegment,
                items = allSegments,
                modifier = Modifier.padding(top = 15.dp, bottom = 10.dp),
                shape = RoundedCornerShape(8.dp),
                checkedColor = Color.White,
                itemWidth = 100.dp,
                backgroundColor = AppTheme.colors.divider,
                checkedTextColor = AppTheme.colors.title,
                textColor = AppTheme.colors.title,
                onCheckedChange = {
                    if (it is ModeSegment) {
                        selectedSegment = it
                    }
                }
            )

            WheelPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                items = modes,
                initialIndex = initialIndex,
                highlightColor = Color(0xFFF2F2F2),
                textColor = Color.Black,
                textSize = 12.sp,
                onPickCompleted = { mode ->
                    onModeChange(mode)
                },
                label = { it.title }
            )
        }
    }
}