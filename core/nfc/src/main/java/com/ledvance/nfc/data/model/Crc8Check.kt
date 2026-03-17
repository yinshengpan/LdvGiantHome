package com.ledvance.nfc.data.model

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 15:12
 * Describe : Crc8Check
 */
data class Crc8Check(
    val crc8: Int,
    val desiredCrc8: Int,
    val faultCode: Int,
    val faultCodeCrc8: Int,
    val desiredFaultCodeCrc8: Int
)