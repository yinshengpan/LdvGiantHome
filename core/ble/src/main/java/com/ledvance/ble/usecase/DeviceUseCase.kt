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
        ensureConnected(mac)
        createProtocol(mac).on() // 示例
        registry.updateActive(mac)
    }

    suspend fun on(mac: String) {
        ensureConnected(mac)
        val protocol = createProtocol(mac)
        protocol.on() // 示例
        registry.updateActive(mac)
    }

    suspend fun off(mac: String) {
        ensureConnected(mac)
        val protocol = createProtocol(mac)
        protocol.off() // 示例
        registry.updateActive(mac)
    }

    suspend fun setHSV(mac: String, h: Int, s: Int, v: Int) {
        ensureConnected(mac)
        createProtocol(mac).setHSV(h, s, v)
        registry.updateActive(mac)
    }

    suspend fun setCCT(mac: String, temp: Int, brightness: Int) {
        ensureConnected(mac)
        createProtocol(mac).setCCT(temp, brightness)
        registry.updateActive(mac)
    }

    suspend fun setScene(mac: String, sceneId: Int) {
        ensureConnected(mac)
        createProtocol(mac).setScene(sceneId)
        registry.updateActive(mac)
    }

    private suspend fun ensureConnected(mac: String) {
        val device = registry.get(mac)
        if (device?.isConnected != true) {
            connectionManager.requestConnect(mac)
            waitConnected(mac)
        }
    }

    private suspend fun waitConnected(mac: String) {
        repeat(50) {
            if (registry.get(mac)?.isConnected == true) return
            delay(300)
        }
        error("connect timeout")
    }

    private fun createProtocol(mac: String): BleProtocol {
        val client = connectionManager.getClient(mac)
            ?: error("no client")

        return GiantProtocol(client, client.commandQueue)
    }
}