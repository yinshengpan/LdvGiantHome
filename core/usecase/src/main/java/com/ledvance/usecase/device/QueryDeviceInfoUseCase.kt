package com.ledvance.usecase.device

import com.ledvance.ble.core.DeviceRegistry
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
) : FlowUseCase<String, DeviceInfo>(dispatcher) {
    override fun execute(parameter: String): Flow<DeviceInfo> {
        return deviceRegistry.devicesFlow.map {
            it.filter { it.address == parameter }.map {
                val rgbToHsv = ColorUtils.rgbToHsv(it.r, it.g, it.b)
                DeviceInfo(
                    address = it.address,
                    name = it.name ?: "",
                    isOnline = it.isOnline,
                    switch = it.power,
                    mode = it.mode,
                    speed = it.speed,
                    h = rgbToHsv[0],
                    s = rgbToHsv[1],
                    v = rgbToHsv[2],
                    w = it.w,
                    brightness = it.brightness
                )
            }.firstOrNull()
        }.filterNotNull().distinctUntilChanged()
    }
}