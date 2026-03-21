package com.ledvance.domain.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:03
 * Describe : DeviceUiItem
 */
data class DeviceUiItem(
    val deviceId: DeviceId,
    val name: String,
    val power: Boolean,
    val deviceType: DeviceType,
    val isOnline: Boolean = false,
)