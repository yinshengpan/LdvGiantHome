package com.ledvance.usecase.device

import com.ledvance.ble.core.ConnectionManager
import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.ble.protocol.BleProtocol
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.command.BrightnessType
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
class DeviceControlUseCase @Inject constructor(
    private val registry: DeviceRegistry,
    private val connectionManager: ConnectionManager,
    private val deviceRepo: DeviceRepo,
) {

    suspend fun switch(address: String, switch: Boolean) {
        ensureConnected(address)
        val protocol = getProtocol(address)
        if (switch) {
            protocol.on()
        } else {
            protocol.off()
        }
        registry.updateActive(address)
        deviceRepo.updateDeviceSwitch(address, switch)
    }

    suspend fun setColourModeHS(address: String, h: Int, s: Int) {
        ensureConnected(address)
        val protocol = getProtocol(address)
        protocol.setHSV(h, s)
        registry.updateActive(address)
    }

    suspend fun setColourModeBrightness(address: String, brightness: Int) {
        ensureConnected(address)
        val protocol = getProtocol(address)
        protocol.setBrightness(BrightnessType.RGB, brightness)
        registry.updateActive(address)
    }

    suspend fun setWhiteModeCCT(address: String, cct: Int) {
        ensureConnected(address)
        getProtocol(address).setCCT(cct)
        registry.updateActive(address)
    }

    suspend fun setWhiteModeBrightness(address: String, brightness: Int) {
        ensureConnected(address)
        val protocol = getProtocol(address)
        protocol.setBrightness(BrightnessType.WCT, brightness)
        registry.updateActive(address)
    }

    suspend fun setScene(address: String, sceneId: Byte) {
        ensureConnected(address)
        getProtocol(address).setScene(sceneId)
        registry.updateActive(address)
    }

    suspend fun queryDeviceInfo(address: String) {
        ensureConnected(address)
        getProtocol(address).queryDeviceInfo()
        registry.updateActive(address)
    }

    private suspend fun ensureConnected(address: String) {
        val device = registry.get(address)
        if (device?.isConnected != true) {
            connectionManager.requestConnect(address)
            waitConnected(address)
        }
    }

    private suspend fun waitConnected(address: String) {
        repeat(50) {
            if (registry.get(address)?.isConnected == true) return
            delay(300)
        }
        error("connect timeout")
    }

    private fun getProtocol(address: String): BleProtocol {
        val client = connectionManager.getClient(address)
            ?: error("no client")

        return client.protocol
    }
}