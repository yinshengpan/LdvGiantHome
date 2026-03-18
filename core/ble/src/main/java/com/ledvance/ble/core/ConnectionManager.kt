package com.ledvance.ble.core

import com.ledvance.ble.bean.BleDeviceState
import com.ledvance.ble.bean.ConnectionState
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

        scope.launch {
            try {
                val client = BleClient(mac, bleRepository) // 你封装 Nordic
                client.connect()
                connectionMap[mac] = client
                registry.updateConnection(mac, ConnectionState.CONNECTED)
            } catch (e: Exception) {
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
        return connectionMap.keys
            .mapNotNull { registry.get(it) }
            .sortedWith(
                compareBy<BleDeviceState> { it.lastActiveTime }
                    .thenBy { it.rssi } // RSSI 越小越先踢
            )
            .first().mac
    }
}