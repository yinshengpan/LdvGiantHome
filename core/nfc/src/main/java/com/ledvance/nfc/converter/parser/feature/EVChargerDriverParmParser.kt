package com.ledvance.nfc.converter.parser.feature

import com.ledvance.nfc.converter.position.EVChargerPosition
import com.ledvance.nfc.data.model.EVChargerParam
import com.ledvance.nfc.utils.getInt
import com.ledvance.nfc.utils.subArray
import com.ledvance.utils.extensions.toByteArray
import com.ledvance.utils.extensions.toHex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 14:52
 * Describe : EVChargerDriverParmParser
 */
internal class EVChargerDriverParmParser : IFeatureParser<EVChargerParam> {
    override suspend fun parse(byteArray: ByteArray): EVChargerParam {
        byteArray.run {
            val sn = parseSN()
            val totalPower = getInt(EVChargerPosition.TotalPower)
            val tripCurrent = getInt(EVChargerPosition.SetTripCurrent).also {
                Timber.tag(TAG).i("parse tripCurrentBytes->${it.toByteArray().toHex()}")
            }.let {
                (it / 100f).toInt()
            }.toString()
            val l1 = getInt(EVChargerPosition.L1)
            val l2 = getInt(EVChargerPosition.L2)
            val l1Voltage = getInt(EVChargerPosition.L1Voltage)
            val l2Voltage = getInt(EVChargerPosition.L2Voltage)
            val l1Power = getInt(EVChargerPosition.L1Power)
            val l2Power = getInt(EVChargerPosition.L2Power)
            Timber.tag(TAG).i("parse: sn->$sn")
            Timber.tag(TAG).i("parse: totalPower->$totalPower")
            Timber.tag(TAG).i("parse: tripCurrent->$tripCurrent")
            Timber.tag(TAG).i("parse: l1->$l1")
            Timber.tag(TAG).i("parse: l2->$l2")
            Timber.tag(TAG).i("parse: l1Voltage->$l1Voltage")
            Timber.tag(TAG).i("parse: l2Voltage->$l2Voltage")
            Timber.tag(TAG).i("parse: l1Power->$l1Power")
            Timber.tag(TAG).i("parse: l2Power->$l2Power")
            return EVChargerParam(sn = sn, tripCurrent = tripCurrent)
        }
    }

    private suspend fun ByteArray.parseSN(): String = withContext(Dispatchers.IO) {
        val position = EVChargerPosition.DeviceSN
        val snArray = subArray(position)
        val sn = snArray.joinToString("-") {
            "%02X".format(it)
        }
        return@withContext sn
    }
}