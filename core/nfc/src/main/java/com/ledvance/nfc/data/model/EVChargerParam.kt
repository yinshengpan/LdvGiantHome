package com.ledvance.nfc.data.model

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 14:45
 * Describe : EVChargerParam
 */
data class EVChargerParam(
    val sn: String,
    val tripCurrent: String,
    val currentRange: IntRange = IntRange(1, 1000)
)