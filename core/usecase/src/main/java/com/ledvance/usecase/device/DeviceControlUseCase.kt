package com.ledvance.usecase.device

import com.ledvance.ble.core.ConnectionManager
import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.ble.protocol.BleProtocol
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.command.common.TimerRepeat
import com.ledvance.domain.bean.command.common.TimerType
import com.ledvance.domain.bean.command.giant.BrightnessType
import com.ledvance.domain.bean.command.giant.DeviceMicRhythm
import com.ledvance.domain.bean.command.giant.LineSequence
import com.ledvance.domain.bean.command.giant.ModeId
import com.ledvance.domain.bean.command.common.ModeType
import com.ledvance.domain.bean.command.giant.scenes.Scene
import com.ledvance.utils.extensions.toUnsignedInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
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
            val newH = h.coerceIn(0, 360)
            val newS = s.coerceIn(0, 100)
            protocol.setHs(newH, newS)
            registry.updateActive(deviceId)
            deviceRepo.updateDeviceHs(deviceId, newH, newS)
        }
    }

    suspend fun setColourModeBrightness(deviceId: DeviceId, brightness: Int): Boolean {
        return executionResult("setColourModeBrightness(deviceId=$deviceId, brightness=$brightness)") {
            ensureConnected(deviceId)
            val protocol = getProtocol(deviceId)
            val newBrightness = brightness.coerceIn(1, 100)
            protocol.setBrightness(BrightnessType.RGB.command.toInt(), newBrightness)
            registry.updateActive(deviceId)
            deviceRepo.updateDeviceV(deviceId, newBrightness)
        }
    }

    suspend fun setRgb(deviceId: DeviceId, r: Int, g: Int, b: Int): Boolean {
        return try {
            ensureConnected(deviceId)
            val protocol = getProtocol(deviceId)
            protocol.setRgb(r, g, b)
            registry.updateActive(deviceId)
            true
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "setRgb")
            false
        }
    }

    suspend fun setBrightness(deviceId: DeviceId, brightness: Int, brightnessType: BrightnessType = BrightnessType.RGB): Boolean {
        return try {
            ensureConnected(deviceId)
            val protocol = getProtocol(deviceId)
            val newBrightness = brightness.coerceIn(1, 100)
            protocol.setBrightness(brightnessType.command.toInt(), newBrightness)
            registry.updateActive(deviceId)
            true
        } catch (e: Exception) {
            Timber.tag(TAG).e(e, "setBrightness")
            false
        }
    }

    suspend fun setWhiteModeCCT(deviceId: DeviceId, cct: Int): Boolean {
        return executionResult("setWhiteModeCCT(deviceId=$deviceId, cct=$cct)") {
            val newCct = cct.coerceIn(0, 100)
            ensureConnected(deviceId)
            getProtocol(deviceId).setCct(newCct)
            registry.updateActive(deviceId)
            deviceRepo.updateDeviceCct(deviceId, newCct)
        }
    }

    suspend fun setWhiteModeBrightness(deviceId: DeviceId, brightness: Int): Boolean {
        return executionResult("setWhiteModeBrightness(deviceId=$deviceId, brightness=$brightness)") {
            ensureConnected(deviceId)
            val protocol = getProtocol(deviceId)
            val newBrightness = brightness.coerceIn(1, 100)
            protocol.setBrightness(BrightnessType.WCT.command.toInt(), newBrightness)
            registry.updateActive(deviceId)
            deviceRepo.updateDeviceBrightness(deviceId, newBrightness)
        }
    }

    suspend fun setScene(deviceId: DeviceId, sceneId: Scene): Boolean {
        return executionResult("setScene(deviceId=$deviceId, sceneId=$sceneId)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).setScene(sceneId.command.toInt())
            registry.updateActive(deviceId)
            val modeId = ModeId.fromInt(sceneId.command.toUnsignedInt())
            deviceRepo.updateDeviceMode(deviceId, ModeType.GiantScene, modeId)
        }
    }

    suspend fun setMode(deviceId: DeviceId, modeId: ModeId): Boolean {
        return executionResult("setMode(deviceId=$deviceId, modeId=$modeId)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).setModeId(modeId.command.toInt())
            registry.updateActive(deviceId)
            deviceRepo.updateDeviceMode(deviceId, ModeType.GiantClassic, modeId)
        }
    }

    suspend fun setModeType(deviceId: DeviceId, modeType: ModeType): Boolean {
        return executionResult("setModeType(deviceId=$deviceId, modeType=$modeType)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).setModeType(modeType.command.toInt())
            registry.updateActive(deviceId)
            deviceRepo.updateDeviceMode(deviceId, modeType, null)
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
            getProtocol(deviceId).setLineSequence(lineSequence.command.toInt())
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

    suspend fun setTimer(
        deviceId: DeviceId,
        timerType: TimerType,
        hour: Int,
        min: Int,
        timerRepeat: TimerRepeat,
        delay: Int = 0
    ): Boolean {
        return executionResult("setTimer(deviceId=$deviceId, timerType=$timerType, hour=$hour, min=$min, timerRepeat=$timerRepeat, delay=$delay)") {
            ensureConnected(deviceId)
            getProtocol(deviceId).setTimer(timerType, hour, min, timerRepeat, delay)
            registry.updateActive(deviceId)
        }
    }

    suspend fun onReconnect(deviceId: DeviceId): Boolean {
        return executionResult("onReconnect(deviceId=$deviceId)") {
            ensureConnected(deviceId)
        }
    }

    suspend fun connectDevice(deviceId: DeviceId): Boolean = withContext(Dispatchers.IO) {
        return@withContext executionResult("connectDevice(deviceId=$deviceId)") {
            ensureConnected(deviceId)
        }
    }

    fun asyncConnectDevice(deviceId: DeviceId) {
        connectionManager.requestConnect(deviceId)
    }

    suspend fun disconnectDevice(deviceId: DeviceId): Boolean {
        return executionResult("disconnectDevice(deviceId=$deviceId)") {
            val device = registry.get(deviceId)
            Timber.tag(TAG).d("disconnectDevice($deviceId) isConnected = ${device?.isConnected}")
            if (device?.isConnected != false) {
                connectionManager.disconnect(deviceId)
                waitDisconnected(deviceId)
            }
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
        Timber.tag(TAG).e("waitConnected: connect timeout for deviceId=%s", deviceId)
        error("connect timeout")
    }

    private suspend fun waitDisconnected(deviceId: DeviceId) {
        repeat(20) {
            if (registry.get(deviceId)?.isConnected == false) return
            delay(300)
        }
        Timber.tag(TAG).e("waitDisconnected: disconnect timeout for deviceId=%s", deviceId)
        error("disconnect timeout")
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