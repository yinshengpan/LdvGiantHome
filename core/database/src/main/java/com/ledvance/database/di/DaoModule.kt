package com.ledvance.database.di

import com.ledvance.database.AppDatabase
import com.ledvance.database.dao.ChargerDao
import com.ledvance.database.dao.DeviceDao
import com.ledvance.database.dao.SetTripCurrentHistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/23 17:48
 * Describe : DaoModule
 */
@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun providesDeviceDao(database: AppDatabase): DeviceDao = database.deviceDao()

    @Provides
    fun providesChargerDao(database: AppDatabase): ChargerDao = database.chargerDao()

    @Provides
    fun providesTripCurrentHistoryDao(database: AppDatabase): SetTripCurrentHistoryDao =
        database.setTripCurrentHistoryDao()
}