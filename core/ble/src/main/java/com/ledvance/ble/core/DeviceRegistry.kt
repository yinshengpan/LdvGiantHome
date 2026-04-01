package com.ledvance.ble.core

import com.ledvance.ble.bean.BleDeviceState
import com.ledvance.ble.bean.ConnectionState
import com.ledvance.ble.bean.ProtocolType
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceTimer
import com.ledvance.domain.bean.command.giant.ModeId
import com.ledvance.domain.bean.command.giant.ModeType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:20
 * Describe : DeviceRegistry — 管理内存中的设备状态，并自动持久化到数据库
 */
@Singleton
class DeviceRegistry @Inject constructor() {
    private val TAG = "DeviceRegistry"
    private val deviceMap = mutableMapOf<DeviceId, BleDeviceState>()

    private val _devicesFlow = MutableStateFlow<List<BleDeviceState>>(emptyList())
    val devicesFlow: StateFlow<List<BleDeviceState>> = _devicesFlow

    fun updateConnection(deviceId: DeviceId, state: ConnectionState) {
        Timber.tag(TAG).d("updateConnection() deviceId = $deviceId, state = $state")
        val old = deviceMap[deviceId] ?: BleDeviceState(
            deviceId = deviceId,
            rssi = 0,
            isConnected = false,
            connectionState = ConnectionState.CONNECTING,
            lastSeenTime = now(),
            lastActiveTime = 0,
            protocolType = ProtocolType.LEDVANCE
        )
        deviceMap[deviceId] = old.copy(
            isConnected = state == ConnectionState.CONNECTED,
            connectionState = state,
        )
        emit()
    }

    /** 同步设备状态到内存并持久化到数据库 */
    fun updateDeviceInfo(
        deviceId: DeviceId,
        power: Boolean,
        r: Int, g: Int, b: Int, w: Int,
        brightness: Int,
        modeType: Int,
        modeId: Int,
        speed: Int
    ) {
        Timber.tag(TAG)
            .d("updateDeviceInfo(): $deviceId, power=$power, RGBW($r,$g,$b,$w), br=$brightness, modeType=$modeType, mode=$modeId, speed=$speed")
        val old = deviceMap[deviceId] ?: return
        deviceMap[deviceId] = old.copy(
            power = power,
            r = r, g = g, b = b, w = w,
            brightness = brightness,
            modeType = ModeType.fromInt(modeType),
            modeId = ModeId.fromInt(modeId),
            speed = speed,
            lastActiveTime = now()
        )
        emit()
    }

    /** 仅更新开关状态 */
    fun updateDeviceState(deviceId: DeviceId, power: Boolean) {
        Timber.tag(TAG).d("updateDeviceState() called with: deviceId = $deviceId, power = $power")
        val old = deviceMap[deviceId] ?: return
        deviceMap[deviceId] = old.copy(
            power = power,
            lastActiveTime = now()
        )
        emit()
    }

    fun updateTimerInfo(deviceId: DeviceId, onTimer: DeviceTimer, offTimer: DeviceTimer) {
        Timber.tag(TAG).d("updateTimerInfo() called with: deviceId = $deviceId, onTimer = $onTimer, offTimer = $offTimer")
        val old = deviceMap[deviceId] ?: return
        deviceMap[deviceId] = old.copy(
            lastActiveTime = now(),
            onTimer = onTimer,
            offTimer = offTimer
        )
        emit()
    }

    fun updateActive(deviceId: DeviceId) {
        val old = deviceMap[deviceId] ?: return
        deviceMap[deviceId] = old.copy(lastActiveTime = now())
        emit()
    }

    /**
     * 扫描发现设备时更新 RSSI 与 lastSeenTime。
     * 仅对已存在条目的设备有效（曾经尝试连接过），
     * 供 AutoConnectUseCase 调用以改善 eviction 策略准确性。
     */
    fun updateRssi(deviceId: DeviceId, rssi: Int) {
        val old = deviceMap[deviceId] ?: return
        deviceMap[deviceId] = old.copy(rssi = rssi, lastSeenTime = now())
        // No emit() — RSSI changes alone don't need to notify UI observers
    }

    private fun emit() {
        _devicesFlow.value = deviceMap.values
            .sortedByDescending { it.rssi }
    }

    fun get(deviceId: DeviceId) = deviceMap[deviceId]

    private fun now() = System.currentTimeMillis()
}