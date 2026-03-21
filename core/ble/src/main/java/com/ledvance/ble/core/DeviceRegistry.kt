package com.ledvance.ble.core

import com.ledvance.ble.bean.BleDeviceState
import com.ledvance.ble.bean.ConnectionState
import com.ledvance.ble.bean.ProtocolType
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceTimer
import com.ledvance.domain.bean.command.ModeId
import com.ledvance.domain.bean.command.ModeType
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

    fun onScanResult(list: List<ScannedDevice>) {
        val now = now()
        var changed = false
        list.forEach { scan ->
            val deviceId = scan.deviceId
            val old = deviceMap[deviceId]

            val newState = old?.copy(
                rssi = scan.rssi,
                lastSeenTime = now,
                isOnline = true
            ) ?: BleDeviceState(
                deviceId = deviceId,
                name = scan.name,
                rssi = scan.rssi,
                isOnline = true,
                isConnected = false,
                connectionState = ConnectionState.DISCONNECTED,
                lastSeenTime = now,
                lastActiveTime = 0,
                protocolType = ProtocolType.LEDVANCE
            )

            deviceMap[deviceId] = newState
            changed = true
        }

        if (changed) emit()
    }

    fun updateConnection(deviceId: DeviceId, state: ConnectionState) {
        val old = deviceMap[deviceId] ?: BleDeviceState(
            deviceId = deviceId,
            name = null,
            rssi = 0,
            isOnline = true,
            isConnected = false,
            connectionState = ConnectionState.DISCONNECTED,
            lastSeenTime = now(),
            lastActiveTime = 0,
            protocolType = ProtocolType.LEDVANCE
        )
        deviceMap[deviceId] = old.copy(
            isConnected = state == ConnectionState.CONNECTED,
            connectionState = state,
            isOnline = state == ConnectionState.CONNECTED || old.isOnline
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
        mode: Int,
        speed: Int
    ) {
        Timber.tag(TAG)
            .d("updateDeviceInfo(): $deviceId, power=$power, RGBW($r,$g,$b,$w), br=$brightness, modeType=$modeType, mode=$mode, speed=$speed")
        val old = deviceMap[deviceId] ?: return
        deviceMap[deviceId] = old.copy(
            power = power,
            r = r, g = g, b = b, w = w,
            brightness = brightness,
            modeType = ModeType.fromInt(modeType),
            modeId = ModeId.fromInt(mode),
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

    fun offlineCheck() {
        val now = now()
        var changed = false

        deviceMap.forEach { (mac, d) ->
            val online = d.isConnected || (now - d.lastSeenTime < 15_000)
            if (d.isOnline != online) {
                deviceMap[mac] = d.copy(isOnline = online)
                changed = true
            }
        }

        if (changed) emit()
    }

    private fun emit() {
        _devicesFlow.value = deviceMap.values
            .sortedByDescending { it.rssi }
    }

    fun get(deviceId: DeviceId) = deviceMap[deviceId]

    private fun now() = System.currentTimeMillis()
}