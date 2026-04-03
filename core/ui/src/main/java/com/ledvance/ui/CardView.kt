package com.ledvance.ui

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 09:42
 * Describe : CardView
 */
@Composable
fun CardView(
    modifier: Modifier = Modifier,
    containerColor: Color = AppTheme.colors.cardBackground,
    contentColor: Color = Color.Unspecified,
    elevation: Dp = 8.dp,
    shape: Shape = RoundedCornerShape(10.dp),
    paddingValues: PaddingValues = PaddingValues(0.dp),
    content: @Composable ColumnScope.() -> Unit
) {
    Card(
        elevation = CardDefaults.cardElevation(elevation),
        colors = CardDefaults.cardColors(containerColor = containerColor, contentColor = contentColor),
        shape = shape,
        modifier = Modifier
            .then(modifier)
            .padding(paddingValues = paddingValues),
        content = content
    )
}