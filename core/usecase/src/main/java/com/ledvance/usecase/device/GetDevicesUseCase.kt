package com.ledvance.usecase.device

import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceUiItem
import com.ledvance.usecase.base.FlowUseCaseWithoutParameter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:05
 * Describe : GetDevicesUseCase
 */
class GetDevicesUseCase(
    dispatcher: CoroutineDispatcher,
    private val deviceRepo: DeviceRepo
) : FlowUseCaseWithoutParameter<List<DeviceUiItem>>(dispatcher) {
    override fun execute(parameter: Unit): Flow<List<DeviceUiItem>> {
        return deviceRepo.getDeviceListFlow().map {
            it.map { it.toDeviceUiItem() }
        }.distinctUntilChanged()
    }
}

internal fun DeviceEntity.toDeviceUiItem(): DeviceUiItem {
    return DeviceUiItem(
        name = name,
        deviceId = deviceId,
        power = power,
        deviceType = deviceType
    )
}

