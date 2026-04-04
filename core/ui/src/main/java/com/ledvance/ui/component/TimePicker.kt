package com.ledvance.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.ui.R
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/19 20:53
 * Describe : TimePicker
 */
@Composable
fun TimePicker(
    initialHour: Int = 0,
    initialMinute: Int = 0,
    onCancel: () -> Unit,
    onConfirm: (hour: Int, minute: Int) -> Unit
) {
    var selectedHour by remember { mutableIntStateOf(initialHour) }
    var selectedMinute by remember { mutableIntStateOf(initialMinute) }

    val hours = remember { (0..23).toList() }
    val minutes = remember { (0..59).toList() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .padding(horizontal = 32.dp)
            .padding(bottom = 24.dp, top = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = stringResource(R.string.select_time),
            fontSize = 19.sp,
            fontWeight = FontWeight.W700,
            color = Color.Black,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Pickers
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(200.dp)
                    .height(64.dp)
                    .background(Color(0xFFFF976E).copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFFFF976E), RoundedCornerShape(12.dp))
            )

            Row(
                modifier = Modifier
                    .width(220.dp)
                    .height(240.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                WheelPicker(
                    modifier = Modifier.weight(1f),
                    items = hours,
                    initialIndex = initialHour,
                    highlightColor = Color.Transparent,
                    textColor = Color.Black,
                    onSelectionChanged = { selectedHour = it },
                    label = { it.toString().padStart(2, '0') }
                )

                Text(
                    text = ":",
                    fontSize = 33.sp,
                    fontWeight = FontWeight.W400,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 2.dp)
                )

                WheelPicker(
                    modifier = Modifier.weight(1f),
                    items = minutes,
                    initialIndex = initialMinute,
                    highlightColor = Color.Transparent,
                    textColor = Color.Black,
                    onSelectionChanged = { selectedMinute = it },
                    label = { it.toString().padStart(2, '0') }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFB5B5B5),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(11.dp)
            ) {
                Text(text = stringResource(R.string.cancel), fontSize = 15.sp, fontWeight = FontWeight.W400)
            }

            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(46.dp)
                    .background(AppTheme.colors.cardBackgroundBrush, RoundedCornerShape(11.dp))
                    .clickable { onConfirm(selectedHour, selectedMinute) },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(R.string.confirm),
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.W400
                )
            }
        }
    }
}
