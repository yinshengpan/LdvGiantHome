package com.ledvance.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
            .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title
        Text(
            text = "Select Time",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        // Pickers
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp), // Match itemHeight(48) * visibleCount(5)
            verticalAlignment = Alignment.CenterVertically
        ) {
            WheelPicker(
                modifier = Modifier.weight(1f),
                items = hours,
                initialIndex = initialHour,
                highlightColor = Color(0xFFF2F2F2),
                textColor = Color.Black,
                onSelectionChanged = { selectedHour = it },
                label = { it.toString().padStart(2, '0') }
            )

            WheelPicker(
                modifier = Modifier.weight(1f),
                items = minutes,
                initialIndex = initialMinute,
                highlightColor = Color(0xFFF2F2F2),
                textColor = Color.Black,
                onSelectionChanged = { selectedMinute = it },
                label = { it.toString().padStart(2, '0') }
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFF2F2F2),
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(text = "Cancel", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }

            Button(
                onClick = { onConfirm(selectedHour, selectedMinute) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppTheme.colors.primary, // Purple from screenshot
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(text = "Confirm", fontSize = 18.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}
