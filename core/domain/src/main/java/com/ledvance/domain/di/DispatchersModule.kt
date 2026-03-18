package com.ledvance.domain.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:15
 * Describe : DispatchersModule
 */

@Module
@InstallIn(SingletonComponent::class)
object DispatchersModule {

    @Provides
    @Dispatcher(Dispatchers.Default)
    fun providesDefaultDispatcher(): CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Default

    @Provides
    @Dispatcher(Dispatchers.IO)
    fun providesIODispatcher(): CoroutineDispatcher = kotlinx.coroutines.Dispatchers.IO

    @Provides
    @Dispatcher(Dispatchers.Main)
    fun providesMainDispatcher(): CoroutineDispatcher = kotlinx.coroutines.Dispatchers.Main
}
