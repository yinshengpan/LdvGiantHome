package com.ledvance.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.ui.R
import com.ledvance.ui.extensions.getFullNameResId
import com.ledvance.ui.theme.AppTheme
import java.time.DayOfWeek

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/3/26 13:43
 * Describe : WeekPicker
 */
@Composable
fun WeekPicker(
    initialDays: Set<DayOfWeek>,
    onCancel: () -> Unit,
    onConfirm: (Set<DayOfWeek>) -> Unit
) {
    var selectedDays by remember { mutableStateOf(initialDays) }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.dialog_select_time_repeat_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val daysOfWeek = DayOfWeek.entries
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(daysOfWeek) { day ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedDays = if (selectedDays.contains(day)) {
                                selectedDays - day
                            } else {
                                selectedDays + day
                            }
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(day.getFullNameResId()),
                        modifier = Modifier.weight(1f),
                        color = Color.Black
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_check_circle),
                        contentDescription = null,
                        tint = if (selectedDays.contains(day)) AppTheme.colors.primary else AppTheme.colors.divider
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F2F2), contentColor = Color.Black),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(text = stringResource(R.string.cancel))
            }

            Button(
                onClick = { onConfirm(selectedDays) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.primary, contentColor = Color.White),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(text = stringResource(R.string.confirm))
            }
        }
    }
}