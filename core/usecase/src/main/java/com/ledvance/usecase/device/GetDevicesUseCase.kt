package com.ledvance.usecase.device

import com.ledvance.database.model.DeviceWithRuntimeConfig
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceUiItem
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.FlowUseCaseWithoutParameter
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:05
 * Describe : GetDevicesUseCase
 */
@ViewModelScoped
class GetDevicesUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
    private val deviceRepo: DeviceRepo
) : FlowUseCaseWithoutParameter<List<DeviceUiItem>>(dispatcher) {
    override fun execute(parameter: Unit): Flow<List<DeviceUiItem>> {
        return deviceRepo.getDeviceListFlow().map {
            it.map { it.toDeviceUiItem() }
        }.distinctUntilChanged()
    }
}

internal fun DeviceWithRuntimeConfig.toDeviceUiItem(): DeviceUiItem {
    return DeviceUiItem(
        name = device.name,
        deviceId = device.deviceId,
        power = device.power,
        deviceType = device.deviceType
    )
}

