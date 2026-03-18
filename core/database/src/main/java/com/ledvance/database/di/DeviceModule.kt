package com.ledvance.database.di

import com.ledvance.database.repo.DeviceRepo
import com.ledvance.database.usecase.AddDeviceUseCase
import com.ledvance.database.usecase.GetDevicesUseCase
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
 * Created date 3/18/26 16:58
 * Describe : DeviceModule
 */
@Module
@InstallIn(ViewModelComponent::class)
internal object DeviceModule {

    @Provides
    @ViewModelScoped
    fun providesAddDeviceUseCase(
        @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
        deviceRepo: DeviceRepo,
    ) = AddDeviceUseCase(dispatcher, deviceRepo)

    @Provides
    @ViewModelScoped
    fun providesGetDevicesUseCase(
        @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
        deviceRepo: DeviceRepo,
    ) = GetDevicesUseCase(dispatcher, deviceRepo)
}