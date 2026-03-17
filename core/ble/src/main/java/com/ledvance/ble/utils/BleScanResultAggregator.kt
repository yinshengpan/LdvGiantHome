package com.ledvance.ble.utils

import android.os.SystemClock
import com.ledvance.ble.bean.ScannedDevice
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
        devices[scanItem.device.address] = ScannedDevice(
            name = scanItem.device.name ?: "",
            address = scanItem.device.address,
            scanTime = SystemClock.elapsedRealtime()
        )
        return devices.map { it.value }
    }

    fun reset() {
        devices.clear()
    }
}