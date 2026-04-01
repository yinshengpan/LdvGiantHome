package com.ledvance.usecase.device

import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.FirmwareVersion
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.SuspendUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 15:45
 * Describe : AddDeviceUseCase
 */
@ViewModelScoped
class AddDeviceUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
    private val deviceRepo: DeviceRepo,
) : SuspendUseCase<AddDeviceUseCase.Param, Unit>(dispatcher) {
    override suspend fun execute(parameter: Param) {
        with(parameter) {
            val device = deviceRepo.getDevice(deviceId)
            if (device != null) {
                Timber.tag(TAG).d("${device.device.name}($deviceId) is already exists")
                return
            }
            deviceRepo.addDevice(
                deviceEntity = DeviceEntity(
                    deviceId = deviceId,
                    name = name,
                    firmwareVersion = FirmwareVersion.create(firmwareVersion),
                    deviceType = DeviceType.fromName(name)
                )
            )
        }
    }

    data class Param(
        val deviceId: DeviceId,
        val name: String,
        val firmwareVersion: Int?,
    )
}