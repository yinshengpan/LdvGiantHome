package com.ledvance.domain.di

import javax.inject.Qualifier

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:02
 * Describe : Dispatchers
 */

@Qualifier
@Retention(AnnotationRetention.RUNTIME)
annotation class Dispatcher(val value: Dispatchers)

enum class Dispatchers {
    Default,
    IO,
    Main,
}
