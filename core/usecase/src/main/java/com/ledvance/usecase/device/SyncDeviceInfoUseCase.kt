package com.ledvance.usecase.device

import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.database.model.DeviceBaseUpdateEntity
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.UseCase
import com.ledvance.utils.ColorUtils
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 14:30
 * Describe : SyncDeviceInfoUseCase
 */
@ViewModelScoped
class SyncDeviceInfoUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) private val dispatcher: CoroutineDispatcher,
    private val deviceRegistry: DeviceRegistry,
    private val deviceRepo: DeviceRepo,
) : UseCase<CoroutineScope, Job>() {
    override fun execute(parameter: CoroutineScope): Job {
        return deviceRegistry.devicesFlow.map {
            it.map { device ->
                val rgbToHsv = ColorUtils.rgbToHsv(device.r, device.g, device.b)
                DeviceBaseUpdateEntity(
                    deviceId = device.deviceId,
                    power = device.power,
                    modeType = device.modeType,
                    modeId = device.modeId,
                    brightness = device.brightness,
                    speed = device.speed,
                    h = rgbToHsv[0],
                    s = rgbToHsv[1],
                    v = device.brightness,
                    cct = device.w,
                )
            }
        }
            .distinctUntilChanged()
            .onEach { stateList ->
                deviceRepo.syncBaseInfoList(stateList)
            }.flowOn(dispatcher)
            .launchIn(parameter)
    }
}