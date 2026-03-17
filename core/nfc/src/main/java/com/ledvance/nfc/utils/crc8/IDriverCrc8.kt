package com.ledvance.nfc.utils.crc8

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/7/24 13:48
 * Describe : IDriverCrc8
 */
internal interface IDriverCrc8 {
    fun calculationDataCrc8(byteArray: ByteArray): Int
    fun calculationFaultCodeCrc8(byteArray: ByteArray): Int
}