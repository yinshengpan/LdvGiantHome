package com.ledvance.nfc.utils.crc8

import com.ledvance.nfc.converter.position.EVChargerPosition
import com.ledvance.nfc.utils.subArray
import com.ledvance.utils.extensions.asInt

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/7/24 13:58
 * Describe : DualChannelCrc8
 */
object EVChargerCrc8 : IDriverCrc8 {
    override fun calculationDataCrc8(byteArray: ByteArray): Int {
        val availableArra = byteArray.subArray(EVChargerPosition.Crc8CalculationRange)
        return ProductDataCrc8.tryCrc8(availableArra).asInt()

    }

    override fun calculationFaultCodeCrc8(byteArray: ByteArray): Int {
        val faultCodeArray = byteArray.subArray(EVChargerPosition.FaultCode)
        val faultCodeCrc8 = ProductDataCrc8.tryCrc8(faultCodeArray).asInt()
        return faultCodeCrc8
    }
}