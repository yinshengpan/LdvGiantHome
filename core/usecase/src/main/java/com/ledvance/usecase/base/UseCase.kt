package com.ledvance.usecase.base

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 13:03
 * Describe : UseCase
 */
abstract class UseCase<in P, out R> {

    operator fun invoke(parameter: P): R = execute(parameter)

    protected abstract fun execute(parameter: P): R
}

abstract class UseCaseWithoutParameter<out R> : UseCase<Unit, R>() {

    operator fun invoke(): R = invoke(parameter = Unit)
}
