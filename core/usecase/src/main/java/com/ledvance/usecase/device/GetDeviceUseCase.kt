package com.ledvance.usecase.device

import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceInfo
import com.ledvance.usecase.base.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 15:33
 * Describe : GetDeviceUseCase
 */
class GetDeviceUseCase(
    dispatcher: CoroutineDispatcher,
    private val deviceRepo: DeviceRepo
) : FlowUseCase<DeviceId, DeviceInfo>(dispatcher) {
    override fun execute(parameter: DeviceId): Flow<DeviceInfo> {
        return deviceRepo.getDeviceFlow(parameter)
            .map { it?.toDeviceInfo() }
            .filterNotNull()
            .distinctUntilChanged()
    }
}

internal fun DeviceEntity.toDeviceInfo(): DeviceInfo {
    return DeviceInfo(
        deviceId = deviceId,
        deviceType = deviceType,
        workMode = workMode,
        name = name,
        power = power,
        modeType = modeType,
        modeId = modeId,
        speed = speed,
        h = h,
        s = s,
        v = v,
        cct = cct,
        brightness = brightness
    )
}
