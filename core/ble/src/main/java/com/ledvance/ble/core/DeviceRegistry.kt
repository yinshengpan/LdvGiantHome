package com.ledvance.ble.core

import com.ledvance.ble.bean.BleDeviceState
import com.ledvance.ble.bean.ConnectionState
import com.ledvance.ble.bean.ProtocolType
import com.ledvance.ble.bean.ScannedDevice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:20
 * Describe : DeviceRegistry
 */
@Singleton
class DeviceRegistry @Inject constructor() {

    private val deviceMap = mutableMapOf<String, BleDeviceState>()

    private val _devicesFlow = MutableStateFlow<List<BleDeviceState>>(emptyList())
    val devicesFlow: StateFlow<List<BleDeviceState>> = _devicesFlow

    fun onScanResult(list: List<ScannedDevice>) {
        val now = now()
        var changed = false

        list.forEach { scan ->
            val mac = scan.address
            val old = deviceMap[mac]

            val newState = old?.copy(
                rssi = scan.rssi,
                lastSeenTime = now,
                isOnline = true
            ) ?: BleDeviceState(
                mac = mac,
                name = scan.name,
                rssi = scan.rssi,
                isOnline = true,
                isConnected = false,
                connectionState = ConnectionState.DISCONNECTED,
                lastSeenTime = now,
                lastActiveTime = 0,
                protocolType = ProtocolType.LEDVANCE
            )

            deviceMap[mac] = newState
            changed = true
        }

        if (changed) emit()
    }

    fun updateConnection(mac: String, state: ConnectionState) {
        val old = deviceMap[mac] ?: BleDeviceState(
            mac = mac,
            name = mac,
            rssi = 0,
            isOnline = true,
            isConnected = false,
            connectionState = ConnectionState.DISCONNECTED,
            lastSeenTime = now(),
            lastActiveTime = 0,
            protocolType = ProtocolType.LEDVANCE
        )
        deviceMap[mac] = old.copy(
            isConnected = state == ConnectionState.CONNECTED,
            connectionState = state,
            isOnline = state == ConnectionState.CONNECTED || old.isOnline
        )
        emit()
    }

    fun updateDeviceInfo(mac: String, power: Boolean, r: Int, g: Int, b: Int, w: Int, brightness: Int, mode: Int, speed: Int) {
        val old = deviceMap[mac] ?: return
        deviceMap[mac] = old.copy(
            power = power,
            r = r, g = g, b = b, w = w,
            brightness = brightness,
            mode = mode,
            speed = speed,
            lastActiveTime = now()
        )
        emit()
    }

    fun updateActive(mac: String) {
        val old = deviceMap[mac] ?: return
        deviceMap[mac] = old.copy(lastActiveTime = now())
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

    fun get(mac: String) = deviceMap[mac]

    private fun now() = System.currentTimeMillis()
}