package com.ledvance.domain.bean

import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/25/26 15:42
 * Describe : Company
 */
@Serializable
enum class Company(val title: String, val type: Int) {
    Unknown("Unknown", 0x00),
    Ledvance("LEDVANCE", 0x01),
    Giant("HYD", 0x02),
    ;

    companion object {
        fun typeOf(type: Int?): Company {
            return entries.find { it.type == type } ?: Unknown
        }
    }
}