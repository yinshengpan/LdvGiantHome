package com.ledvance.ble.bean

import kotlin.math.roundToInt

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 09:12
 * Describe : Configuration
 */
data class Configuration(val type: DeviceConfiguration, private val value: String) {
    fun getValue(): Number {
        return when (type) {
            is DeviceConfiguration.ChargeCurrent -> value.toIntOrNull()?.let { it / 10f } ?: 0
            DeviceConfiguration.L1 -> value.toIntOrNull()?.let { it / 10f } ?: 0
            DeviceConfiguration.L2 -> value.toIntOrNull()?.let { it / 10f } ?: 0
            DeviceConfiguration.L3 -> value.toIntOrNull()?.let { it / 10f } ?: 0
            DeviceConfiguration.TripCurrent -> value.toIntOrNull()?.let { it / 100f } ?: 0

            else -> 0
        }
    }
}