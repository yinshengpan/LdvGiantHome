package com.ledvance.domain.bean

import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 10:53
 * Describe : DeviceId
 */

@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
data class DeviceId(val macAddress: String, val deviceType: DeviceType) {
    override fun toString() = macAddress
}

fun DeviceId.asMacAddress() = macAddress

