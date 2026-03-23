package com.ledvance.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.ledvance.ui.theme.AppTheme
import kotlinx.coroutines.delay

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/22 08:49
 * Describe : LoadingOverlay with fade animation and integrated feel.
 */
@Composable
fun LoadingOverlay(
    visible: Boolean,
    modifier: Modifier = Modifier
) {
    var showPopup by remember { mutableStateOf(false) }
    
    LaunchedEffect(visible) {
        if (visible) {
            showPopup = true
        } else {
            // Wait for fade out animation to complete
            delay(300)
            showPopup = false
        }
    }

    if (showPopup) {
        Popup(
            onDismissRequest = {},
            properties = PopupProperties(
                focusable = true,
                dismissOnBackPress = false,
                dismissOnClickOutside = false,
                excludeFromSystemGesture = true,
            )
        ) {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(),
                exit = fadeOut(),
                modifier = modifier
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = {}
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Card(
                        shape = RoundedCornerShape(15.dp),
                        elevation = CardDefaults.elevatedCardElevation(10.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = AppTheme.colors.dialogBackground
                        )
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(28.dp)
                        ) {
                            CircularProgressIndicator(
                                color = AppTheme.colors.primary,
                                strokeWidth = 4.dp
                            )
                        }
                    }
                }
            }
        }
    }
}
