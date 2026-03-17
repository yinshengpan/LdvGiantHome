package com.ledvance.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/4 19:18
 * Describe : CoroutineScopeUtils
 */
object CoroutineScopeUtils {
    private val coroutineScope by lazy {
        CoroutineScope(Dispatchers.IO)
    }

    fun launch(
        context: CoroutineContext = EmptyCoroutineContext,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ) {
        coroutineScope.launch(context, start, block)
    }
}