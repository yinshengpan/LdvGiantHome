package com.ledvance.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.cancellation.CancellationException

abstract class FlowUseCase<in P, out R> constructor(
    private val coroutineContext: CoroutineContext,
) {

    operator fun invoke(parameter: P): Flow<R> =
        try {
            execute(parameter)
        } catch (ce: CancellationException) {
            throw ce
        } catch (e: Exception) {
            emptyFlow()
        }.flowOn(coroutineContext)

    protected abstract fun execute(parameter: P): Flow<R>
}

abstract class FlowUseCaseWithoutParameter<out R> constructor(
    coroutineContext: CoroutineContext,
) : FlowUseCase<Unit, R>(coroutineContext) {

    operator fun invoke(): Flow<R> = invoke(parameter = Unit)
}
