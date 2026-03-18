package com.ledvance.ble.usecase

import com.ledvance.ble.core.CommandQueue
import com.ledvance.ble.core.ConnectionManager
import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.ble.protocol.BleProtocol
import com.ledvance.ble.protocol.GiantProtocol
import kotlinx.coroutines.delay
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:25
 * Describe : DeviceUseCase
 */
@Singleton
class DeviceUseCase @Inject constructor(
    private val registry: DeviceRegistry,
    private val connectionManager: ConnectionManager
) {

    suspend fun toggle(mac: String) {
        val device = registry.get(mac) ?: return

        connectionManager.requestConnect(mac)

        waitConnected(mac)

        val protocol = createProtocol(mac)

        protocol.on() // 示例

        registry.updateActive(mac)
    }

    suspend fun on(mac: String) {
        connectionManager.requestConnect(mac)

        waitConnected(mac)

        val protocol = createProtocol(mac)

        protocol.on() // 示例

        registry.updateActive(mac)
    }

    suspend fun off(mac: String) {
        connectionManager.requestConnect(mac)

//        waitConnected(mac)

        val protocol = createProtocol(mac)

        protocol.on() // 示例

        registry.updateActive(mac)
    }

    suspend fun setHSV(mac: String, h: Int, s: Int, v: Int) {
        connectionManager.requestConnect(mac)
        waitConnected(mac)

        createProtocol(mac).setHSV(h, s, v)
        registry.updateActive(mac)
    }

    private suspend fun waitConnected(mac: String) {
        repeat(10) {
            if (registry.get(mac)?.isConnected == true) return
            delay(300)
        }
        error("connect timeout")
    }

    private fun createProtocol(mac: String): BleProtocol {
        val client = connectionManager.getClient(mac)
            ?: error("no client")

        return GiantProtocol(client, CommandQueue())
    }
}