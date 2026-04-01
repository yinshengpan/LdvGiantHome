package com.ledvance.ble.core.parser

import com.ledvance.domain.bean.DeviceId

/**
 * @author : generated
 * Describe : BleNotificationParser
 */
interface BleNotificationParser {
    val TAG: String
    fun parse(deviceId: DeviceId, bytes: ByteArray)
}
