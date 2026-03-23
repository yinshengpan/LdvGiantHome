package com.ledvance.light.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/22 10:36
 * Describe : CardFeatureItem
 */
internal sealed class CardFeature(
    val title: String,
    val enable: Boolean? = null
) {
    data object Scene : CardFeature(title = "Scene", enable = false)
    data object Timer : CardFeature(title = "Timer", enable = false)
    data object Music : CardFeature(title = "Music")
    data object Mode : CardFeature(title = "Mode", enable = false)
}

@Composable
internal fun CardFeatureItem(cardFeature: CardFeature, onItemClick: (CardFeature) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp, bottom = 15.dp)
            .height(84.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.screenBackground),
        elevation = CardDefaults.elevatedCardElevation(7.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .debouncedClickable { onItemClick(cardFeature) }
        ) {
            if (cardFeature.enable != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(10.dp)
                        .size(8.dp)
                        .background(
                            color = if (cardFeature.enable) AppTheme.colors.progress else AppTheme.colors.progressBackground,
                            shape = CircleShape
                        )
                )
            }
            Text(
                text = cardFeature.title,
                modifier = Modifier.align(Alignment.Center),
                color = AppTheme.colors.title,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium
            )
        }
    }
}