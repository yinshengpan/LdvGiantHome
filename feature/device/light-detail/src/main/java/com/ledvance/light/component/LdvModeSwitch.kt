package com.ledvance.light.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.FlowRowScope
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.domain.bean.command.common.ModeType
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.extensions.getIconResId
import com.ledvance.ui.extensions.getNameResId
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/3/26 11:38
 * Describe : LdvModeSwitch
 */
@Composable
internal fun LdvModeSwitch(selectedMode: ModeType, modeList: List<ModeType>, onModeTypeChange: (ModeType) -> Unit) {
    FlowRow(
        maxItemsInEachRow = 2,
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
    ) {
        modeList.forEach { mode ->
            ModeItem(
                mode = mode,
                isSelected = selectedMode == mode,
                onClick = { onModeTypeChange(mode) }
            )
        }
    }
}

@Composable
private fun FlowRowScope.ModeItem(
    mode: ModeType,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(112.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = if (isSelected) {
                Modifier
                    .fillMaxSize()
                    .background(brush = AppTheme.colors.cardBackgroundBrush)
                    .debouncedClickable { onClick() }
            } else {
                Modifier
                    .fillMaxSize()
                    .background(color = AppTheme.colors.cardBackground)
                    .debouncedClickable { onClick() }
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            color = if (isSelected) Color(0x33FFFFFF) else Color(0xFFF3F3F3),
                            shape = RoundedCornerShape(12.dp),
                        ), contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = mode.getIconResId()),
                        contentDescription = null,
                        tint = if (isSelected) Color.White else Color(0xFFFF976E),
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = stringResource(id = mode.getNameResId()),
                    color = if (isSelected) Color.White else AppTheme.colors.title,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}