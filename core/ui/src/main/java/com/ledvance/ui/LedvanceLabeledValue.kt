package com.ledvance.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ledvance.ui.extensions.toDp
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/7/10 16:06
 * Describe : LedvanceLabeledValue
 */
@Composable
fun LedvanceLabeledValue(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    unit: String? = null,
    titleColor: Color = AppTheme.colors.secondaryTitle,
    contentColor: Color = AppTheme.colors.textFieldContent,
    unitColor: Color = AppTheme.colors.textFieldUnit,
    contentTextAlign: TextAlign = TextAlign.End,
    showBorder: Boolean = false
) {

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(modifier),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = AppTheme.typography.bodyMedium,
            color = titleColor,
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .height(34.dp)
                .then(
                    if (showBorder) Modifier.border(
                        width = 1.toDp(),
                        color = AppTheme.colors.textFieldSecondaryBorder,
                        shape = RoundedCornerShape(4.dp)
                    ) else modifier
                ),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = value,
                style = AppTheme.typography.bodySmall.copy(
                    color = contentColor,
                    textAlign = contentTextAlign
                ),
                maxLines = 1,
                modifier = Modifier
                    .padding(horizontal = 2.dp)

            )
            if (!unit.isNullOrEmpty()) {
                Text(
                    text = unit,
                    style = AppTheme.typography.bodySmall,
                    color = unitColor,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(end = 10.dp)
                )
            }
        }
    }
}