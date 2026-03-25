package com.ledvance.ota.di

import com.ledvance.ota.repository.GiantOtaRepositoryImpl
import com.ledvance.ota.domain.repository.OtaRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class OtaModule {

    @Binds
    @Singleton
    abstract fun bindOtaRepository(
        giantOtaRepositoryImpl: GiantOtaRepositoryImpl
    ): OtaRepository
}
