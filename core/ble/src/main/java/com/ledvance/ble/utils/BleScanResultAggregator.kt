package com.ledvance.ble.utils

import android.os.SystemClock
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.ble.constant.Constants
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceType
import no.nordicsemi.android.kotlin.ble.core.scanner.BleScanResult

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 17:36
 * Describe : BleScanResultAggregator
 */
class BleScanResultAggregator {
    private val devices = mutableMapOf<String, ScannedDevice>()
    fun aggregateDevices(scanItem: BleScanResult): List<ScannedDevice> {
        val name = scanItem.device.name ?: ""
        if (!name.startsWith(Constants.BLE_PREFIX)) {
            return devices.map { it.value }
        }
        val rssi = scanItem.data?.rssi ?: -99
        val isConnectable = rssi > -85
        if (isConnectable) {
            devices[scanItem.device.address] = ScannedDevice(
                name = name,
                deviceId = DeviceId(scanItem.device.address),
                rssi = rssi,
                deviceType = DeviceType.fromName(name),
                scanTime = SystemClock.elapsedRealtime()
            )
        } else {
            devices.remove(scanItem.device.address)
        }
        return devices.map { it.value }
    }

    fun reset() {
        devices.clear()
    }
}