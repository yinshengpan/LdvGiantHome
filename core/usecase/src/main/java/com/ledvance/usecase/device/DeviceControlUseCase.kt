package com.ledvance.usecase.device

import com.ledvance.ble.core.ConnectionManager
import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.ble.protocol.BleProtocol
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.TimerType
import com.ledvance.domain.bean.command.BrightnessType
import com.ledvance.domain.bean.command.ModeId
import com.ledvance.domain.bean.command.scenes.Scene
import kotlinx.coroutines.delay
import timber.log.Timber
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
    private val TAG = "DeviceControlUseCase"

    suspend fun setPower(deviceId: DeviceId, power: Boolean): Boolean {
        return executionResult {
            ensureConnected(deviceId)
            val protocol = getProtocol(deviceId)
            protocol.setPower(power)
            registry.updateActive(deviceId)
            deviceRepo.updateDevicePower(deviceId, power)
        }
    }

    suspend fun setColourModeHS(deviceId: DeviceId, h: Int, s: Int): Boolean {
        return executionResult {
            ensureConnected(deviceId)
            val protocol = getProtocol(deviceId)
            protocol.setHSV(h, s)
            registry.updateActive(deviceId)
        }
    }

    suspend fun setColourModeBrightness(deviceId: DeviceId, brightness: Int): Boolean {
        return executionResult {
            ensureConnected(deviceId)
            val protocol = getProtocol(deviceId)
            protocol.setBrightness(BrightnessType.RGB, brightness)
            registry.updateActive(deviceId)
        }
    }

    suspend fun setWhiteModeCCT(deviceId: DeviceId, cct: Int): Boolean {
        return executionResult {
            ensureConnected(deviceId)
            getProtocol(deviceId).setCCT(cct)
            registry.updateActive(deviceId)
        }
    }

    suspend fun setWhiteModeBrightness(deviceId: DeviceId, brightness: Int): Boolean {
        return executionResult {
            ensureConnected(deviceId)
            val protocol = getProtocol(deviceId)
            protocol.setBrightness(BrightnessType.WCT, brightness)
            registry.updateActive(deviceId)
        }
    }

    suspend fun setScene(deviceId: DeviceId, sceneId: Scene): Boolean {
        return executionResult {
            ensureConnected(deviceId)
            getProtocol(deviceId).setScene(sceneId)
            registry.updateActive(deviceId)
        }
    }

    suspend fun setMode(deviceId: DeviceId, modeId: ModeId): Boolean {
        return executionResult {
            ensureConnected(deviceId)
            getProtocol(deviceId).setMode(modeId)
            registry.updateActive(deviceId)
        }
    }

    suspend fun setSpeed(deviceId: DeviceId, speed: Int): Boolean {
        return executionResult {
            ensureConnected(deviceId)
            getProtocol(deviceId).setSpeed(speed)
            registry.updateActive(deviceId)
        }
    }

    suspend fun queryDeviceInfo(deviceId: DeviceId): Boolean {
        return executionResult {
            ensureConnected(deviceId)
            getProtocol(deviceId).queryDeviceInfo()
            registry.updateActive(deviceId)
        }
    }

    suspend fun syncDeviceTime(deviceId: DeviceId): Boolean {
        return executionResult {
            ensureConnected(deviceId)
            getProtocol(deviceId).syncCurrentTime()
            registry.updateActive(deviceId)
        }
    }

    suspend fun queryTimer(deviceId: DeviceId): Boolean {
        return executionResult {
            ensureConnected(deviceId)
            getProtocol(deviceId).queryTimer()
            registry.updateActive(deviceId)
        }
    }

    suspend fun setTimer(deviceId: DeviceId, timerType: TimerType, hour: Int, min: Int, weekCycle: Int): Boolean {
        return executionResult {
            ensureConnected(deviceId)
            getProtocol(deviceId).setTimer(timerType, hour, min, weekCycle)
            registry.updateActive(deviceId)
        }
    }

    private suspend fun ensureConnected(deviceId: DeviceId) {
        val device = registry.get(deviceId)
        if (device?.isConnected != true) {
            connectionManager.requestConnect(deviceId)
            waitConnected(deviceId)
        }
    }

    private suspend fun waitConnected(deviceId: DeviceId) {
        repeat(30) {
            if (registry.get(deviceId)?.isConnected == true) return
            delay(300)
        }
        error("connect timeout")
    }

    private fun getProtocol(deviceId: DeviceId): BleProtocol {
        val client = connectionManager.getClient(deviceId)
            ?: error("no client")

        return client.protocol
    }

    private suspend fun executionResult(block: suspend () -> Unit): Boolean {
        return try {
            block()
            true
        } catch (e: Exception) {
            Timber.tag(TAG).e(e)
            false
        }
    }
}