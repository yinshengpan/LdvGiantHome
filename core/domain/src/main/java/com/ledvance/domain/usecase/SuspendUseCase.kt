package com.ledvance.domain.usecase

import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 13:03
 * Describe : SuspendUseCase
 */
abstract class SuspendUseCase<in P, R> constructor(
    private val coroutineContext: CoroutineContext,
) {

    suspend operator fun invoke(parameter: P): Result<R> =
        try {
            withContext(coroutineContext) {
                preExecute(parameter)
                execute(parameter).let {
                    Result.success(it)
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }.also { result ->
            postExecute(parameter, result)
        }

    protected abstract suspend fun execute(parameter: P): R

    protected open fun preExecute(parameter: P) = Unit

    protected open fun postExecute(parameter: P, result: Result<R>) = Unit
}

abstract class SuspendUseCaseWithoutParameter<R> constructor(
    coroutineContext: CoroutineContext,
) : SuspendUseCase<Unit, R>(coroutineContext) {

    suspend operator fun invoke(): Result<R> = invoke(parameter = Unit)
}