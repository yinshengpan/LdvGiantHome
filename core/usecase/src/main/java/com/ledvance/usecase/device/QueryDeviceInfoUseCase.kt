package com.ledvance.usecase.device

import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceInfo
import com.ledvance.usecase.base.FlowUseCase
import com.ledvance.utils.ColorUtils
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 15:09
 * Describe : QueryDeviceInfoUseCase
 */
class QueryDeviceInfoUseCase(
    dispatcher: CoroutineDispatcher,
    private val deviceRegistry: DeviceRegistry,
) : FlowUseCase<DeviceId, DeviceInfo>(dispatcher) {
    override fun execute(parameter: DeviceId): Flow<DeviceInfo> {
        return deviceRegistry.devicesFlow.map {
            it.filter { it.deviceId == parameter }.map {
                val rgbToHsv = ColorUtils.rgbToHsv(it.r, it.g, it.b)
                DeviceInfo(
                    deviceId = it.deviceId,
                    name = it.name ?: "",
                    isOnline = it.isOnline,
                    power = it.power,
                    modeType = it.modeType,
                    mode = it.mode,
                    speed = it.speed,
                    h = rgbToHsv[0],
                    s = rgbToHsv[1],
                    v = it.brightness, // v 转 RGB 是默认是 100
                    w = it.w,
                    brightness = it.brightness
                )
            }.firstOrNull()
        }.filterNotNull().distinctUntilChanged()
    }
}