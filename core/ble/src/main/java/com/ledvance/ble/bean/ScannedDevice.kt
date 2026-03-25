package com.ledvance.ble.bean

import com.ledvance.ble.constant.Constants
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceType
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/10/25 14:47
 * Describe : ScannedDevice
 */
@Serializable
data class ScannedDevice(
    val deviceId: DeviceId,
    val name: String,
    val rssi: Int = 0,
    val deviceType: DeviceType = DeviceType.GiantTable,
    val scanTime: Long = 0,
) {
    fun hasOutOfRange(elapsedRealtime: Long) = elapsedRealtime - scanTime > 60000
}

fun ScannedDevice.parseGiantFirmwareVersion(): Int? {
    if (name.length < 5) return null
    if (!name.startsWith(Constants.BLE_PREFIX_GIANT)) return null
    val hex = name.substring(3, 5)
    return hex.toIntOrNull(16)
}