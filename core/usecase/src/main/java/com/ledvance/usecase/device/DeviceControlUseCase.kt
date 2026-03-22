package com.ledvance.usecase.device

import com.ledvance.ble.core.ConnectionManager
import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.ble.protocol.BleProtocol
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.TimerType
import com.ledvance.domain.bean.command.BrightnessType
import com.ledvance.domain.bean.command.DeviceMicRhythm
import com.ledvance.domain.bean.command.LineSequence
import com.ledvance.domain.bean.command.ModeId
import com.ledvance.domain.bean.command.ModeType
import com.ledvance.domain.bean.command.scenes.Scene
import com.ledvance.utils.extensions.toUnsignedInt
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
        return executionResult("setPower(deviceId=$deviceId, power=$power)") {
            ensureConnected(deviceId)
            val protocol = getProtocol(deviceId)
            protocol.setPower(power)
            registry.updateActive(deviceId)
            deviceRepo.updateDevicePower(deviceId, power)
        }
    }

    suspend fun setColourModeHS(deviceId: DeviceId, h: Int, s: Int): Boolean {
        return executionResult("setColourModeHS(deviceId=$deviceId, h=$h, s=$s)") {
            ensureConnected(deviceId)
            val protocol = getProtocol(deviceId)
            protocol.setHSV(h, s)
            registry.updateActive(deviceId)
            deviceRepo.updateDeviceHs(deviceId, h, s)
        }
    }

    suspend fun setColourModeBrightness(deviceId: DeviceId, brightness: Int): Boolean {
        return executionResult("setColourModeBrightness(deviceId=$deviceId, brightness=$brightness)") {
            ensureConnected(deviceId)
            val protocol = getProtocol(deviceId)
            protocol.setBrightness(BrightnessType.RGB, brightness)
            registry.updateActive(deviceId)
            deviceRepo.updateDeviceV(deviceId, brightness)
        }
    }

    suspend fun setWhiteModeCCT(deviceId: DeviceId, cct: Int): Boolean {
        return executionResult("setWhiteModeCCT(deviceId=$deviceId, cct=$cct)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).setCCT(cct)
            registry.updateActive(deviceId)
            deviceRepo.updateDeviceCct(deviceId, cct)
        }
    }

    suspend fun setWhiteModeBrightness(deviceId: DeviceId, brightness: Int): Boolean {
        return executionResult("setWhiteModeBrightness(deviceId=$deviceId, brightness=$brightness)") {
            ensureConnected(deviceId)
            val protocol = getProtocol(deviceId)
            protocol.setBrightness(BrightnessType.WCT, brightness)
            registry.updateActive(deviceId)
            deviceRepo.updateDeviceBrightness(deviceId, brightness)
        }
    }

    suspend fun setScene(deviceId: DeviceId, sceneId: Scene): Boolean {
        return executionResult("setScene(deviceId=$deviceId, sceneId=$sceneId)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).setScene(sceneId)
            registry.updateActive(deviceId)
            val modeId = ModeId.fromInt(sceneId.command.toUnsignedInt())
            deviceRepo.updateDeviceMode(deviceId, ModeType.Scene, modeId)
        }
    }

    suspend fun setMode(deviceId: DeviceId, modeId: ModeId): Boolean {
        return executionResult("setMode(deviceId=$deviceId, modeId=$modeId)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).setMode(modeId)
            registry.updateActive(deviceId)
            deviceRepo.updateDeviceMode(deviceId, ModeType.Classic, modeId)
        }
    }

    suspend fun setSpeed(deviceId: DeviceId, speed: Int): Boolean {
        return executionResult("setSpeed(deviceId=$deviceId, speed=$speed)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).setSpeed(speed)
            registry.updateActive(deviceId)
            deviceRepo.updateDeviceSpeed(deviceId, speed)
        }
    }

    suspend fun reset(deviceId: DeviceId): Boolean {
        return executionResult("reset(deviceId=$deviceId)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).resetDevice()
            registry.updateActive(deviceId)
        }
    }

    suspend fun setLineSequence(deviceId: DeviceId, lineSequence: LineSequence): Boolean {
        return executionResult("setLineSequence(deviceId=$deviceId, lineSequence=$lineSequence)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).setLineSequence(lineSequence)
            registry.updateActive(deviceId)
            deviceRepo.updateDeviceLineSequence(deviceId, lineSequence)
        }
    }

    suspend fun setDeviceMicRhythm(deviceId: DeviceId, deviceMicRhythm: DeviceMicRhythm): Boolean {
        return executionResult("setDeviceMicRhythm(deviceId=$deviceId, deviceMicRhythm=$deviceMicRhythm)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).setMicRhythmEffect(deviceMicRhythm.command.toUnsignedInt())
            registry.updateActive(deviceId)
        }
    }

    suspend fun setDeviceMicSensitivity(deviceId: DeviceId, sensitivity: Int): Boolean {
        return executionResult("setDeviceMicSensitivity(deviceId=$deviceId, sensitivity=$sensitivity)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).setMicSensitivity(sensitivity)
            registry.updateActive(deviceId)
        }
    }

    suspend fun queryDeviceInfo(deviceId: DeviceId): Boolean {
        return executionResult("queryDeviceInfo(deviceId=$deviceId)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).queryDeviceInfo()
            registry.updateActive(deviceId)
        }
    }

    suspend fun syncDeviceTime(deviceId: DeviceId): Boolean {
        return executionResult("syncDeviceTime(deviceId=$deviceId)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).syncCurrentTime()
            registry.updateActive(deviceId)
        }
    }

    suspend fun queryTimer(deviceId: DeviceId): Boolean {
        return executionResult("queryTimer(deviceId=$deviceId)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).queryTimer()
            registry.updateActive(deviceId)
        }
    }

    suspend fun setTimer(deviceId: DeviceId, timerType: TimerType, hour: Int, min: Int, weekCycle: Int): Boolean {
        return executionResult("setTimer(deviceId=$deviceId, timerType=$timerType, hour=$hour, min=$min, weekCycle=$weekCycle)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).setTimer(timerType, hour, min, weekCycle)
            registry.updateActive(deviceId)
        }
    }

    suspend fun onReconnect(deviceId: DeviceId): Boolean {
        return executionResult("onReconnect(deviceId=$deviceId)") {
            ensureConnected(deviceId)
        }
    }

    suspend fun readFirmwareVersion(deviceId: DeviceId): String? {
        val callInfo = "readFirmwareVersion(deviceId=$deviceId)"
        Timber.tag(TAG).d("--> START $callInfo")
        return try {
            ensureConnected(deviceId)
            val client = connectionManager.getClient(deviceId)
            val firmwareVersion = client?.readFirmwareVersion()
            registry.updateActive(deviceId)
            Timber.tag(TAG).d("<-- END $callInfo: success firmwareVersion=$firmwareVersion")
            firmwareVersion
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "<-- END $callInfo: failed")
            null
        }
    }

    private suspend fun ensureConnected(deviceId: DeviceId) {
        val device = registry.get(deviceId)
        Timber.tag(TAG).d("ensureConnected($deviceId) isConnected = ${device?.isConnected}")
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

    private suspend fun executionResult(callInfo: String, block: suspend () -> Unit): Boolean {
        Timber.tag(TAG).d("--> START $callInfo")
        return try {
            block()
            Timber.tag(TAG).d("<-- END $callInfo: result=true")
            true
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "<-- END $callInfo: result=false")
            false
        }
    }
}