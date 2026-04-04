package com.ledvance.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/2 10:05
 * Describe : LedvanceButton
 */
@Composable
fun LedvanceButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .then(modifier)
            .height(46.dp)
    ) {
        FilledTonalButton(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = AppTheme.colors.cardBackgroundBrush,
                    shape = RoundedCornerShape(12.dp)
                ),
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = Color.Transparent,
                contentColor = AppTheme.colors.buttonContent,
            )
        ) {
            Text(
                text = text,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                fontWeight = FontWeight.W400,
                fontSize = 15.sp
            )
        }
    }
}