package com.ledvance.usecase.device

import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceState
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.FlowUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 14:10
 * Describe : GetDeviceStateUseCase
 */
@ViewModelScoped
class GetDeviceStateUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
    private val deviceRegistry: DeviceRegistry
) : FlowUseCase<DeviceId, DeviceState?>(dispatcher) {
    override fun execute(parameter: DeviceId): Flow<DeviceState?> {
        return deviceRegistry.devicesFlow.map {
            it.filter { it.deviceId == parameter }
                .map { device -> (DeviceState(deviceId = device.deviceId, isOnline = device.isOnline, switch = device.power)) }
                .firstOrNull()
        }.distinctUntilChanged()
    }
}