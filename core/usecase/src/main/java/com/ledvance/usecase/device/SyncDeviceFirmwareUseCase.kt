package com.ledvance.usecase.device

import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.SuspendUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/21 17:47
 * Describe : GetDeviceFirmwareUseCase
 */
@ViewModelScoped
class SyncDeviceFirmwareUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
    private val deviceControlUseCase: DeviceControlUseCase,
    private val deviceRepo: DeviceRepo,
) : SuspendUseCase<DeviceId, Unit>(dispatcher) {
    override suspend fun execute(parameter: DeviceId) {
        val readFirmwareVersion = deviceControlUseCase.readFirmwareVersion(parameter)
        readFirmwareVersion?.also {
            deviceRepo.updateDeviceFirmwareVersion(parameter, it)
        }
    }

}