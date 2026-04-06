package com.ledvance.setting

import androidx.compose.foundation.Image
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
import androidx.compose.ui.text.style.TextAlign
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
            .background(Color.White, RoundedCornerShape(topStart = 22.dp, topEnd = 22.dp))
            .padding(horizontal = 32.dp)
            .padding(bottom = 24.dp, top = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.dialog_select_line_sequence_title),
            fontSize = 19.sp,
            fontWeight = FontWeight.W700,
            color = Color.Black,
            textAlign = TextAlign.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
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
                        style = AppTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W400
                        ),
                        color = Color.Black
                    )
                    Image(
                        painter = painterResource(if (selectedSequence == sequence) R.mipmap.icon_checked else R.mipmap.icon_unchecked),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
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
                    .clickable { onConfirm(selectedSequence) },
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
