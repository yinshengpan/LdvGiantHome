package com.ledvance.nfc.converter.parser

import com.ledvance.nfc.converter.parser.feature.Feature
import com.ledvance.nfc.converter.position.EVChargerPosition
import com.ledvance.nfc.converter.serializer.FeatureConverter
import com.ledvance.nfc.data.model.Crc8Check
import com.ledvance.nfc.data.model.DriverInfo
import com.ledvance.nfc.data.model.DriverModel
import com.ledvance.nfc.utils.crc8.EVChargerCrc8
import com.ledvance.nfc.utils.getInt
import com.ledvance.nfc.utils.subArray
import com.ledvance.utils.extensions.toHex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/20/25 11:08
 * Describe : ChargerParser
 */
class EVChargerParser : IDriverParser {
    private val TAG = "EVChargerParser"

    override suspend fun parse(
        driverInfo: DriverInfo,
        byteArray: ByteArray
    ): DriverModel = withContext(Dispatchers.IO) {
        val sn = byteArray.parseSN()
        val crc8Check = byteArray.getCrc8Check()
        val driverModel = FeatureConverter.FeatureParserChain(driverInfo, crc8Check, byteArray)
            .apply(Feature.EVChargerDriverParam)
            .result()
        return@withContext driverModel
    }

    private suspend fun ByteArray.parseSN(): String = withContext(Dispatchers.IO) {
        val position = EVChargerPosition.DeviceSN
        val snArray = subArray(position)
        Timber.tag(TAG).i("parseSN: hex:${snArray.toHex()}")
        val sn = snArray.joinToString("-") {
            "%02X".format(it)
        }
        Timber.tag(TAG).i("parseSN: sn->$sn")
        return@withContext sn
    }

    private fun ByteArray.getCrc8Check(): Crc8Check {
        val crc8 = getInt(EVChargerPosition.Crc8)
        val desiredCrc8 = EVChargerCrc8.calculationDataCrc8(this)
        val faultCode = getInt(EVChargerPosition.FaultCode)
        val faultCodeCrc8 = getInt(EVChargerPosition.FaultCodeCrc8)
        val desiredFaultCodeCrc8 = EVChargerCrc8.calculationFaultCodeCrc8(this)
        val crc8Check = Crc8Check(
            crc8 = crc8,
            desiredCrc8 = desiredCrc8,
            faultCode = faultCode,
            faultCodeCrc8 = faultCodeCrc8,
            desiredFaultCodeCrc8 = desiredFaultCodeCrc8
        )
        Timber.tag(TAG).i("getCrc8Check: crc8Check->$crc8Check")
        return crc8Check
    }
}