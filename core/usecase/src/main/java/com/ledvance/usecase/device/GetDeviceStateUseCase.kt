package com.ledvance.usecase.device

import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.domain.bean.DeviceState
import com.ledvance.usecase.base.FlowUseCaseWithoutParameter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 14:10
 * Describe : GetDeviceStateUseCase
 */
class GetDeviceStateUseCase(
    dispatcher: CoroutineDispatcher,
    private val deviceRegistry: DeviceRegistry
) : FlowUseCaseWithoutParameter<List<DeviceState>>(dispatcher) {
    override fun execute(parameter: Unit): Flow<List<DeviceState>> {
        return deviceRegistry.devicesFlow.map {
            it.map { device -> (DeviceState(address = device.address, isOnline = device.isOnline, switch = device.power)) }
        }.distinctUntilChanged()
    }
}