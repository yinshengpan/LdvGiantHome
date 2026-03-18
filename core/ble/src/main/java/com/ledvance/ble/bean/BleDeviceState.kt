package com.ledvance.ble.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:20
 * Describe : BleDeviceState
 */
data class BleDeviceState(
    val mac: String,
    val name: String?,
    val rssi: Int,

    val isOnline: Boolean,
    val isConnected: Boolean,
    val connectionState: ConnectionState,

    val lastSeenTime: Long,
    val lastActiveTime: Long,

    val protocolType: ProtocolType
)

enum class ConnectionState {
    DISCONNECTED, CONNECTING, CONNECTED, FAILED
}

enum class ProtocolType {
    LEDVANCE
}