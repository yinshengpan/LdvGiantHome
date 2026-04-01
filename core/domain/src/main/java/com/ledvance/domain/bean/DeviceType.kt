package com.ledvance.domain.bean

import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 15:16
 * Describe : DeviceType
 */
@Serializable
enum class DeviceType(val type: Int, val company: Company) {
    GiantTable(0, Company.Giant),
    GiantFloor(1, Company.Giant),
    LdvBedside(2, Company.Ledvance),
    ;

    companion object {
        fun fromName(name: String): DeviceType {
            val upName = name.uppercase()
            return when {
                upName.startsWith(Company.Giant.title) -> when {
                    upName.contains("TABLE") -> GiantTable
                    upName.contains("FLOOR") -> GiantFloor
                    upName.contains("BEDSIDE") -> GiantFloor
                    else -> GiantTable
                }

                else -> GiantTable
            }
        }
        fun fromType(type: Int): DeviceType {
            return entries.find { it.type == type } ?: GiantTable
        }
    }
}