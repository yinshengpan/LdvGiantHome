package com.ledvance.ble.core

import com.ledvance.ble.bean.BleDeviceState
import com.ledvance.ble.bean.ConnectionState
import com.ledvance.ble.protocol.GiantProtocol
import com.ledvance.ble.repo.BleRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:22
 * Describe : ConnectionManager
 */
@Singleton
class ConnectionManager @Inject constructor(
    private val registry: DeviceRegistry,
    private val bleRepository: BleRepository,
) {

    private val scope = CoroutineScope(Dispatchers.IO)

    private val connectionMap = mutableMapOf<String, BleClient>()

    private val MAX_CONNECTION = 3

    fun requestConnect(mac: String) {
        if (connectionMap.containsKey(mac)) return

        if (connectionMap.size >= MAX_CONNECTION) {
            val evict = selectEvictDevice()
            disconnect(evict)
        }

        connect(mac)
    }

    private fun connect(mac: String) {
        registry.updateConnection(mac, ConnectionState.CONNECTING)

        val client = BleClient(mac, bleRepository) { bytes ->
            if (bytes.size == 14 && bytes[0] == 0x1D.toByte() && bytes[1] == 0x00.toByte() && bytes[2] == 0x02.toByte()) {
                val power = bytes[3] == 0x0F.toByte()
                val r = bytes[4].toInt() and 0xFF
                val g = bytes[5].toInt() and 0xFF
                val b = bytes[6].toInt() and 0xFF
                val w = bytes[7].toInt() and 0xFF
                val brightness = bytes[8].toInt() and 0xFF
                val modeId = bytes[10].toInt() and 0xFF
                val speed = bytes[11].toInt() and 0xFF
                registry.updateDeviceInfo(mac, power, r, g, b, w, brightness, modeId, speed)
            }
        }
        connectionMap[mac] = client

        scope.launch {
            try {
                client.connect()
                GiantProtocol(client,client.commandQueue).queryDeviceInfo()
                registry.updateConnection(mac, ConnectionState.CONNECTED)
            } catch (e: Exception) {
                connectionMap.remove(mac)
                registry.updateConnection(mac, ConnectionState.FAILED)
            }
        }
    }

    fun disconnect(mac: String) {
        connectionMap.remove(mac)?.disconnect()
        registry.updateConnection(mac, ConnectionState.DISCONNECTED)
    }

    fun getClient(mac: String): BleClient? = connectionMap[mac]

    private fun selectEvictDevice(): String {
        val now = System.currentTimeMillis()
        val idleThreshold = 60_000L * 5 // 5 minutes

        return connectionMap.keys
            .mapNotNull { registry.get(it) }
            .sortedWith(
                compareBy<BleDeviceState> { state ->
                    // Group devices idle for > 5 mins together as '0L' so they are evicted first based on RSSI
                    if (now - state.lastActiveTime > idleThreshold) 0L else state.lastActiveTime
                }.thenBy { it.rssi } // Lowest RSSI evicted first among ties/idle devices
            )
            .first().address
    }
}