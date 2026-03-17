package com.ledvance.ble.bean

import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/10/25 14:47
 * Describe : ScannedDevice
 */
@Serializable
data class ScannedDevice(
    val name: String,
    val address: String,
    val sn: String = "",
    val scanTime: Long = 0,
) {
    fun hasOutOfRange(elapsedRealtime: Long) = elapsedRealtime - scanTime > 60000
}