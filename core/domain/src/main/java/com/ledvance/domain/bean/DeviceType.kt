package com.ledvance.domain.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 15:16
 * Describe : DeviceType
 */
enum class DeviceType(val type: Int) {
    Table(0),
    Floor(1)
    ;

    companion object {
        fun fromName(name: String): DeviceType {
            return when {
                name.contains("TABLE") -> Table
                name.contains("FLOOR") -> Floor
                else -> Table
            }
        }
        fun fromType(type: Int): DeviceType {
            return entries.find { it.type == type } ?: DeviceType.Table
        }
    }
}