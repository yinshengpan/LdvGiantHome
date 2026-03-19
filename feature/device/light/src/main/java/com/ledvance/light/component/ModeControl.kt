package com.ledvance.light.component

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ledvance.domain.bean.command.MagicColorMode
import com.ledvance.light.bean.ModeSegment
import com.ledvance.ui.component.IRadioGroupItem
import com.ledvance.ui.component.LedvanceRadioGroup
import com.ledvance.ui.component.WheelPicker
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 18:16
 * Describe : ModeControl
 */
@Composable
fun ModeControl(
    selectedModeId: Int,
    onModeChange: (Int) -> Unit
) {
    val allSegments = remember { ModeSegment.allModeSegment }
    var selectedSegment by remember {
        mutableStateOf<IRadioGroupItem<String>>(ModeSegment.Base)
    }

    val modes by remember(selectedSegment) {
        derivedStateOf { MagicColorMode.getModesBySegment(selectedSegment.value) }
    }

    val initialIndex = remember(modes) {
        val index = modes.indexOfFirst { it.id == selectedModeId }
        if (index != -1) index else 0
    }

    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.screenBackground),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            LedvanceRadioGroup(
                selectorItem = selectedSegment,
                items = allSegments,
                modifier = Modifier.padding(top = 15.dp, bottom = 10.dp),
                shape = RoundedCornerShape(8.dp),
                checkedColor = Color.White,
                backgroundColor = AppTheme.colors.divider,
                checkedTextColor = AppTheme.colors.title,
                textColor = AppTheme.colors.title,
                onCheckedChange = {
                    selectedSegment = it
                }
            )

            WheelPicker(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                items = modes,
                initialIndex = initialIndex,
                onSelectionChanged = { mode ->
                    onModeChange(mode.id)
                },
                label = { "${it.id}、 ${it.name}" }
            )
        }
    }
}