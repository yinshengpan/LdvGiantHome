package com.ledvance.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.ui.R
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/4/4 13:54
 * Describe : DelayPicker for selecting minutes from 0 to 120
 */
@Composable
fun DelayPicker(
    initialMinutes: Int = 0,
    onCancel: () -> Unit,
    onConfirm: (minutes: Int) -> Unit
) {
    var selectedMinutes by remember { mutableIntStateOf(initialMinutes) }
    val range = remember { (0..120).toList() }

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
            text = stringResource(R.string.timer_delay),
            fontSize = 19.sp,
            fontWeight = FontWeight.W700,
            color = Color.Black,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp)
        )

        // Picker
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .width(100.dp) // 减小宽度，仅包裹数字部分
                    .height(64.dp)
                    .background(Color(0xFFFF976E).copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                    .border(2.dp, Color(0xFFFF976E), RoundedCornerShape(12.dp))
            )

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Box(
                    modifier = Modifier.width(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    WheelPicker(
                        modifier = Modifier.fillMaxWidth(),
                        items = range,
                        initialIndex = initialMinutes,
                        highlightColor = Color.Transparent,
                        textColor = Color.Black,
                        onSelectionChanged = { selectedMinutes = it },
                        label = { it.toString() }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(140.dp)) // Offset by picker width
                    Text(
                        text = stringResource(R.string.unit_min),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.W400,
                        color = Color.Black,
                        modifier = Modifier.padding(start = 12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

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
                    .clickable { onConfirm(selectedMinutes) },
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

@Preview(showBackground = true)
@Composable
private fun DelayPickerPreview() {
    DelayPicker(onCancel = {}, onConfirm = {})
}
