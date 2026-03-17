package com.ledvance.ui.extensions

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import kotlinx.coroutines.CoroutineScope

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/18 14:58
 * Describe : UIExtensions
 */
@Composable
fun Dp.toPx(): Float {
    val density = LocalDensity.current
    return with(density) { this@toPx.toPx() }
}

@Composable
fun Int.toDp(): Dp {
    val density = LocalDensity.current
    return with(density) { this@toDp.toDp() }
}


@Composable
fun stringResourceFormat(@StringRes id: Int, vararg formatArgs: Any): String {
    val text = stringResource(id)
    return remember(id, *formatArgs) {
        var replacedText = text
        formatArgs.forEachIndexed { index, _ ->
            val placeholder = "[[placeholder${index + 1}]]"
            val formatStr = "%${index + 1}\$s"
            replacedText = replacedText.replace(placeholder, formatStr)
        }
        replacedText.format(*formatArgs)
    }
}

@Composable
fun InitializeScope(block: suspend CoroutineScope.() -> Unit) {
    var initialized by rememberSaveable { mutableStateOf(false) }
    LaunchedEffect(initialized) {
        if (!initialized) {
            block()
            initialized = true
        }
    }
}

@Composable
fun ComposableLifecycle(onEvent: (Lifecycle.Event) -> Unit) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val onEventChange by rememberUpdatedState(onEvent)
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            onEventChange.invoke(event)
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
}