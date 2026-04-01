package com.ledvance.nfc.converter.parser

import com.ledvance.nfc.converter.serializer.FeatureConverter
import com.ledvance.nfc.data.model.Crc8Check
import com.ledvance.nfc.data.model.NfcInfo
import com.ledvance.nfc.data.model.NfcModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/20/25 11:08
 * Describe : BedsideLampParser
 */
internal class BedsideLampParser : INfcParser {
    private val TAG = "BedsideLampParser"

    override suspend fun parse(
        nfcInfo: NfcInfo,
        byteArray: ByteArray
    ): NfcModel = withContext(Dispatchers.IO) {
        val crc8Check = byteArray.getCrc8Check()
        val driverModel = FeatureConverter.FeatureParserChain(nfcInfo, crc8Check, byteArray)
            .result()
        return@withContext driverModel
    }

    private fun ByteArray.getCrc8Check(): Crc8Check {
        val crc8Check = Crc8Check(
            crc8 = 0,
            desiredCrc8 = 0,
            faultCode = 0,
            faultCodeCrc8 = 0,
            desiredFaultCodeCrc8 = 0
        )
        return crc8Check
    }
}