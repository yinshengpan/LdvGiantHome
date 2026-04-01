package com.ledvance.ble.core

import com.ledvance.ble.protocol.BleProtocol
import com.ledvance.ble.protocol.GiantProtocol
import com.ledvance.ble.protocol.LdvBedsideProtocol
import com.ledvance.ble.core.parser.BleNotificationParser
import com.ledvance.ble.core.parser.GiantNotificationParser
import com.ledvance.ble.core.parser.LdvBedsideNotificationParser
import com.ledvance.domain.bean.Company
import com.ledvance.domain.bean.DeviceId

object ProtocolFactory {
    fun createProtocol(
        deviceId: DeviceId,
        client: BleClient,
        commandQueue: CommandQueue
    ): BleProtocol {
        return when (deviceId.deviceType.company) {
            Company.Ledvance -> LdvBedsideProtocol(client, commandQueue)
            else -> GiantProtocol(client, commandQueue)
        }
    }

    fun createParser(
        deviceId: DeviceId,
        registry: DeviceRegistry
    ): BleNotificationParser {
        return when (deviceId.deviceType.company) {
            Company.Ledvance -> LdvBedsideNotificationParser(registry)
            else -> GiantNotificationParser(registry)
        }
    }
}
