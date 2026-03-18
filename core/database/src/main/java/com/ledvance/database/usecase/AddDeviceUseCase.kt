package com.ledvance.database.usecase

import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.usecase.SuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 15:45
 * Describe : AddDeviceUseCase
 */
class AddDeviceUseCase(
    dispatcher: CoroutineDispatcher,
    private val deviceRepo: DeviceRepo,
) : SuspendUseCase<AddDeviceUseCase.Param, Unit>(dispatcher) {
    override suspend fun execute(parameter: Param) {
        with(parameter) {
            deviceRepo.addDevice(
                deviceEntity = DeviceEntity(
                    address = address,
                    name = name,
                    deviceType = DeviceType.fromName(name)
                )
            )
        }
    }

    data class Param(
        val address: String,
        val name: String
    )
}