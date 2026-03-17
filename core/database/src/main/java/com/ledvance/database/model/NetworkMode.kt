package com.ledvance.database.model

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 09:11
 * Describe : NetworkMode
 */
enum class NetworkMode(val type: Int) {
    WIFI(0),
    MOBILE_4G(1),
    NONE(255)//0xFF
    ;

    companion object {
        fun fromType(type: Int): NetworkMode {
            return entries.find { it.type == type } ?: NONE
        }
    }
}