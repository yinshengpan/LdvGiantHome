package com.ledvance.setting

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.domain.bean.command.giant.LineSequence
import com.ledvance.ui.R
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/21/26 21:26
 * Describe : LineSequencePicker
 */
@Composable
internal fun LineSequencePicker(
    initialSequence: LineSequence?,
    onCancel: () -> Unit,
    onConfirm: (LineSequence) -> Unit
) {
    var selectedSequence by remember { mutableStateOf(initialSequence ?: LineSequence.RGB) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.dialog_select_line_sequence_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val sequences = LineSequence.items
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(sequences) { sequence ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedSequence = sequence
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sequence.title,
                        modifier = Modifier.weight(1f),
                        color = Color.Black
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_check_circle),
                        contentDescription = null,
                        tint = if (selectedSequence == sequence) AppTheme.colors.primary else AppTheme.colors.divider
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
                onClick = { onConfirm(selectedSequence) },
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
