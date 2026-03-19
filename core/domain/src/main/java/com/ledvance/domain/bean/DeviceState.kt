package com.ledvance.domain.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 14:07
 * Describe : DeviceState
 */
data class DeviceState(
    val address: String,
    val isOnline: Boolean,
    val switch: Boolean,
)