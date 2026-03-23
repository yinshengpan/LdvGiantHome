package com.ledvance.usecase.device

import com.ledvance.database.model.DeviceWithRuntimeConfig
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceInfo
import com.ledvance.domain.bean.WorkMode
import com.ledvance.domain.bean.command.LineSequence
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.FlowUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 15:33
 * Describe : GetDeviceUseCase
 */
@ViewModelScoped
class GetDeviceUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
    private val deviceRepo: DeviceRepo
) : FlowUseCase<DeviceId, DeviceInfo>(dispatcher) {

    override fun execute(parameter: DeviceId): Flow<DeviceInfo> {
        Timber.tag("GetDeviceUseCase").d("invoke: deviceId=%s", parameter)
        return deviceRepo.getDeviceFlow(parameter)
            .map { it?.toDeviceInfo() }
            .filterNotNull()
            .distinctUntilChanged()
    }
}

internal fun DeviceWithRuntimeConfig.toDeviceInfo(): DeviceInfo {
    return DeviceInfo(
        deviceId = device.deviceId,
        deviceType = device.deviceType,
        workMode = config?.workMode ?: WorkMode.Colour,
        lineSequence = config?.lineSequence ?: LineSequence.RGB,
        name = device.name,
        power = device.power,
        modeType = device.modeType,
        modeId = device.modeId,
        speed = device.speed,
        phoneMicSensitivity = config?.phoneMicSensitivity ?: 60,
        h = device.h,
        s = device.s,
        v = device.v,
        cct = device.cct,
        brightness = device.brightness
    )
}
