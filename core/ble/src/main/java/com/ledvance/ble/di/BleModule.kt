package com.ledvance.ble.di

import com.ledvance.ble.usecase.DeviceSwitchUseCase
import com.ledvance.ble.usecase.DeviceUseCase
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 18:09
 * Describe : BleModule
 */
@Module
@InstallIn(ViewModelComponent::class)
internal object BleModule {

    @Provides
    @ViewModelScoped
    fun providesDeviceSwitchUseCase(
        @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
        deviceUseCase: DeviceUseCase,
    ) = DeviceSwitchUseCase(dispatcher, deviceUseCase)
}