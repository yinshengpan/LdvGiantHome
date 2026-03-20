package com.ledvance.domain.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 15:11
 * Describe : DeviceInfo
 */
data class DeviceInfo(
    val deviceId: DeviceId,
    val name: String,
    val isOnline: Boolean,
    val power: Boolean,
    val modeType: Int = 0,
    val mode: Int = 0,
    val speed: Int = 50,
    val h: Int = 255,
    val s: Int = 255,
    val v: Int = 255,
    val w: Int = 0,
    val brightness: Int = 100,
)