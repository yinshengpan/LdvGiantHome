package com.ledvance.nfc.data.di

import com.ledvance.nfc.data.repository.NfcDataRepository
import com.ledvance.nfc.data.repository.NfcDataRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/23 13:46
 * Describe : DataModule
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class DataModule {
    @Binds
    internal abstract fun bindsNfcDataRepository(
        nfcDataRepository: NfcDataRepositoryImpl
    ): NfcDataRepository
}