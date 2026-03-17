package com.ledvance.ui.extensions

import androidx.compose.foundation.Indication
import androidx.compose.foundation.LocalIndication
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.Interaction
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/4 08:54
 * Describe : ModifierExtensions
 */
@Composable
inline fun Modifier.clickableNonRipples(crossinline onClick: () -> Unit) = then(
    Modifier.debouncedClickable(
        interactionSource = NoRippleInteractionSource(),
        indication = null,
        onClick = onClick,
    )
)

class NoRippleInteractionSource : MutableInteractionSource {
    override val interactions: Flow<Interaction>
        get() = emptyFlow()

    override suspend fun emit(interaction: Interaction) {}
    override fun tryEmit(interaction: Interaction) = true
}

@Composable
inline fun Modifier.debouncedClickable(
    enable: Boolean = true,
    debouncedInterval: Long = 800L,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = LocalIndication.current,
    crossinline onClick: () -> Unit
): Modifier {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    return clickable(
        enabled = enable,
        interactionSource = interactionSource,
        indication = indication,
        onClick = {
            val currentTime = System.currentTimeMillis()
            if (currentTime - lastClickTime > debouncedInterval) {
                lastClickTime = currentTime
                onClick()
            }
        }
    )
}

@Composable
inline fun Modifier.clickCount(
    count: Int,
    interactionSource: MutableInteractionSource? = null,
    indication: Indication? = null,
    crossinline onClick: () -> Unit
): Modifier {
    var lastClickTime by remember { mutableLongStateOf(0L) }
    var countSum by remember { mutableLongStateOf(0L) }
    return clickable(
        interactionSource = interactionSource,
        indication = indication,
        onClick = {
            val currentTime = System.currentTimeMillis()
            if (lastClickTime == 0L || currentTime - lastClickTime < 800L) {
                countSum++
                if (countSum >= count) {
                    onClick.invoke()
                }
            } else {
                lastClickTime = 0L
                countSum = 0
            }
        }
    )
}
