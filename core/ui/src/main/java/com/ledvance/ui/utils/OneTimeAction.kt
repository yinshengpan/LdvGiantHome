package com.ledvance.vivares.directeasy.core.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

/**
 * Action type that should executed only once.
 *
 * Typically it is a business event which not requires any UI
 * (like a screen navigation) or which connects to the UI but
 * should not stored in the UI state (like a list scroll).
 */
interface OneTimeAction

/**
 * Consumer contains the latest [OneTimeAction]s in [actionFlow].
 *
 * Typically a ViewModel's contract includes this contract.
 */
interface OneTimeActionConsumerContract<T : OneTimeAction> {

    /**
     * Current action flow
     */
    val actionFlow: Flow<T>
}

/**
 * Publisher that extends the [OneTimeActionConsumerContract].
 * It contains the latest [OneTimeAction]s in [actionFlow] and [mutableActionFlow],
 * and can handle publish events with [publish] and [tryPublish].
 *
 * Typically a ViewModel includes this contract.
 */
interface OneTimeActionPublisherContract<T : OneTimeAction> : OneTimeActionConsumerContract<T> {

    companion object {
        const val DefaultExtraBufferCapacity: Int = 100
    }

    /**
     * Mutable action flow
     */
    val mutableActionFlow: MutableSharedFlow<T>

    override val actionFlow: Flow<T>
        get() = mutableActionFlow

    /**
     * Publishes an [action]
     */
    suspend fun publish(action: T) {
        mutableActionFlow.emit(action)
    }

    /**
     * Tries to publish an [action],
     * return whether it was successfully or not.
     */
    fun tryPublish(action: T): Boolean = mutableActionFlow.tryEmit(action)
}

/**
 * Creates a mutable action flow with default parameters.
 */
@Suppress("NOTHING_TO_INLINE")
inline fun <T : OneTimeAction> OneTimeActionPublisherContract<T>.createDefaultMutableActionFlow(): MutableSharedFlow<T> =
    MutableSharedFlow(extraBufferCapacity = OneTimeActionPublisherContract.DefaultExtraBufferCapacity)

/**
 * [OneTimeAction] handler that collects [OneTimeActionConsumerContract.actionFlow]
 * and invokes [consume] with the latest action from a coroutine.
 */
@Composable
fun <T : OneTimeAction> OneTimeActionConsumerContract<T>.OneTimeActionEffect(
    consume: suspend (action: T) -> Unit,
) {
    OneTimeActionEffect(
        actionFlow = actionFlow,
        consume = consume,
    )
}

/**
 * [OneTimeAction] handler that collects [actionFlow]
 * and invokes [consume] with the latest action from a coroutine.
 */
@Composable
fun <T : OneTimeAction> OneTimeActionEffect(
    actionFlow: Flow<T>,
    consume: suspend (action: T) -> Unit,
) {
    val rememberedConsume by rememberUpdatedState(newValue = consume)
    LaunchedEffect(actionFlow) {
        actionFlow.collect { action ->
            rememberedConsume(action)
        }
    }
}
