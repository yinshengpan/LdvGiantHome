package com.ledvance.usecase.di

import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.database.repo.TimerRepo
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.device.AddDeviceUseCase
import com.ledvance.usecase.device.DeviceControlUseCase
import com.ledvance.usecase.device.GetAllDeviceIdUseCase
import com.ledvance.usecase.device.GetDeviceListStateUseCase
import com.ledvance.usecase.device.GetDeviceStateUseCase
import com.ledvance.usecase.device.GetDeviceTimersUseCase
import com.ledvance.usecase.device.GetDeviceUseCase
import com.ledvance.usecase.device.GetDevicesUseCase
import com.ledvance.usecase.device.SyncDeviceInfoUseCase
import com.ledvance.usecase.device.SyncDeviceTimerUseCase
import com.ledvance.usecase.device.UpdateDeviceTimerUseCase
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
    fun providesGetDeviceListStateUseCase(
        @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
        deviceRegistry: DeviceRegistry,
    ) = GetDeviceListStateUseCase(dispatcher, deviceRegistry)

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
    fun providesSyncDeviceTimerUseCase(
        @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
        deviceRegistry: DeviceRegistry,
        timerRepo: TimerRepo,
    ) = SyncDeviceTimerUseCase(dispatcher, deviceRegistry, timerRepo)

    @Provides
    @ViewModelScoped
    fun providesGetAllDeviceIdUseCase(
        @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
        deviceRepo: DeviceRepo,
    ) = GetAllDeviceIdUseCase(dispatcher, deviceRepo)

    @Provides
    @ViewModelScoped
    fun providesGetDeviceTimersUseCase(
        @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
        timerRepo: TimerRepo,
    ) = GetDeviceTimersUseCase(dispatcher, timerRepo)

    @Provides
    @ViewModelScoped
    fun providesUpdateDeviceTimerUseCase(
        @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
        timerRepo: TimerRepo,
        deviceControlUseCase: DeviceControlUseCase,
    ) = UpdateDeviceTimerUseCase(dispatcher, timerRepo, deviceControlUseCase)
}