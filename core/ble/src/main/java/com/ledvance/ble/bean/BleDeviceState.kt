package com.ledvance.ble.bean

import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceTimer
import com.ledvance.domain.bean.command.giant.ModeId
import com.ledvance.domain.bean.command.common.ModeType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:20
 * Describe : BleDeviceState
 */
data class BleDeviceState(
    val deviceId: DeviceId,
    val rssi: Int,

    val isConnected: Boolean,
    val connectionState: ConnectionState,

    val lastSeenTime: Long,
    val lastActiveTime: Long,

    val protocolType: ProtocolType,

    val power: Boolean = false,
    val modeType: ModeType? = null,
    val modeId: ModeId? = null,
    val speed: Int = 50,
    val brightness: Int = 100,
    val r: Int = 255,
    val g: Int = 255,
    val b: Int = 255,
    val w: Int = 0,

    val onTimer: DeviceTimer? = null,
    val offTimer: DeviceTimer? = null,
)

enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED, FAILED
}

enum class ProtocolType {
    LEDVANCE
}