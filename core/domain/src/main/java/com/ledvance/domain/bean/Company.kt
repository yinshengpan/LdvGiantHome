package com.ledvance.domain.bean

import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/25/26 15:42
 * Describe : Company
 */
@Serializable
enum class Company(val title: String) {
    Giant("HYD"),
    Ledvance("LDV"),
}