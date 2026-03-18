package com.ledvance.ble.core

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withTimeout

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:23
 * Describe : CommandQueue
 */
class CommandQueue {

    private val mutex = Mutex()

    suspend fun <T> execute(
        retry: Int = 2,
        timeout: Long = 5_000,
        block: suspend () -> T
    ): T {
        mutex.withLock {
            repeat(retry) { attempt ->
                try {
                    return withTimeout(timeout) {
                        block()
                    }
                } catch (e: Exception) {
                    if (attempt == retry - 1) throw e
                }
            }
            error("unreachable")
        }
    }
}