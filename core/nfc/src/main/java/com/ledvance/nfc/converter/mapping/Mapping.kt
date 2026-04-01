package com.ledvance.nfc.converter.mapping

import com.ledvance.domain.bean.DeviceType
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/2/26 09:38
 * Describe : Mapping
 */
@Serializable
sealed class Mapping(val version: Int) {
    data object BedsideLamp : Mapping(1)
    data object Unknown : Mapping(-1)

    companion object {
        fun valueOf(version: Int): Mapping {
            return when (version) {
                BedsideLamp.version -> BedsideLamp
                else -> Unknown
            }
        }

        fun deviceTypeOf(type: DeviceType): Mapping {
            return when (type) {
                DeviceType.LdvBedside -> BedsideLamp
                else -> Unknown
            }
        }
    }
}