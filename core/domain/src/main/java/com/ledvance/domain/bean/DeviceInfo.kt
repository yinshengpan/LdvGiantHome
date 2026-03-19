package com.ledvance.domain.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 15:11
 * Describe : DeviceInfo
 */
data class DeviceInfo(
    val address: String,
    val name: String,
    val isOnline: Boolean,
    val switch: Boolean,
    val mode: Int = 0,
    val speed: Int = 50,
    val h: Int = 255,
    val s: Int = 255,
    val v: Int = 255,
    val w: Int = 0,
    val brightness: Int = 100,
)