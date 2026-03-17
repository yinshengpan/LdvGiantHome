package com.ledvance.utils.extensions

import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/4 18:43
 * Describe : TryCatchExtensions
 */
inline fun tryCatch(isIgnoreLog: Boolean = false, block: () -> Unit) {
    try {
        block()
    } catch (e: Throwable) {
        if (!isIgnoreLog) {
            Timber.tag("TryCatchExtensions")
                .e("the exception in tryCatch -> ${e.stackTraceToString()}")
        }
    }
}

inline fun <reified T> tryCatchReturn(isIgnoreLog: Boolean = false, block: () -> T): T? {
    return try {
        block()
    } catch (e: Throwable) {
        if (!isIgnoreLog) {
            Timber.tag("TryCatchExtensions")
                .e("the exception in tryCatch -> ${e.stackTraceToString()}")
        }
        null
    }
}