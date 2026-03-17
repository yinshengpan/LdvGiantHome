package com.ledvance.database.model

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 09:15
 * Describe : BoxType
 */
enum class BoxType(val type: Int) {
    US(1),
    EU(2),
    CN(3),
    ;

    companion object {
        fun fromType(type: Int): BoxType {
            return entries.find { it.type == type } ?: US
        }
    }
}