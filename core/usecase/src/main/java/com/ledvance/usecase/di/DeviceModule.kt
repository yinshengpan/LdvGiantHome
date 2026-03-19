package com.ledvance.usecase.di

import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.device.AddDeviceUseCase
import com.ledvance.usecase.device.GetAllDeviceIdUseCase
import com.ledvance.usecase.device.GetDeviceStateUseCase
import com.ledvance.usecase.device.GetDeviceUseCase
import com.ledvance.usecase.device.GetDevicesUseCase
import com.ledvance.usecase.device.QueryDeviceInfoUseCase
import com.ledvance.usecase.device.SyncDeviceInfoUseCase
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

    @Provides
    @ViewModelScoped
    fun providesGetDeviceUseCase(
        @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
        deviceRepo: DeviceRepo,
    ) = GetDeviceUseCase(dispatcher, deviceRepo)

    @Provides
    @ViewModelScoped
    fun providesGetDeviceStateUseCase(
        @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
        deviceRegistry: DeviceRegistry,
    ) = GetDeviceStateUseCase(dispatcher, deviceRegistry)

    @Provides
    @ViewModelScoped
    fun providesSyncDeviceInfoUseCase(
        @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
        deviceRegistry: DeviceRegistry,
        deviceRepo: DeviceRepo,
    ) = SyncDeviceInfoUseCase(dispatcher, deviceRegistry, deviceRepo)

    @Provides
    @ViewModelScoped
    fun providesQueryDeviceInfoUseCase(
        @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
        deviceRegistry: DeviceRegistry,
    ) = QueryDeviceInfoUseCase(dispatcher, deviceRegistry)

    @Provides
    @ViewModelScoped
    fun providesGetAllDeviceIdUseCase(
        @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
        deviceRepo: DeviceRepo,
    ) = GetAllDeviceIdUseCase(dispatcher, deviceRepo)
}