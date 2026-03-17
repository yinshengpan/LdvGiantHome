package com.ledvance.database.model

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 17:23
 * Describe : SetTripCurrentType
 */
enum class SetTripCurrentType(val type: Int) {
    Ble(1),
    Nfc(2);

    companion object {
        fun fromType(type: Int): SetTripCurrentType {
            return SetTripCurrentType.entries.find { it.type == type } ?: Ble
        }
    }
}