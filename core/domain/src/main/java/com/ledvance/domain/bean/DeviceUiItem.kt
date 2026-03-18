package com.ledvance.domain.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:03
 * Describe : DeviceUiItem
 */
data class DeviceUiItem(
    val name: String,
    val address: String,
    val switch: Boolean,
    val deviceType: DeviceType
)