package com.ledvance.nfc.converter.serializer.feature

import com.ledvance.nfc.converter.position.EVChargerPosition
import com.ledvance.nfc.data.model.DriverModel
import com.ledvance.nfc.utils.replaceRangeValue
import com.ledvance.utils.extensions.toByteArray
import com.ledvance.utils.extensions.toHex
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 14:43
 * Describe : EVChargerDriverParamSerializer
 */
internal class EVChargerDriverParamSerializer : IFeatureSerializer {
    override fun serialize(driverModel: DriverModel, byteArray: ByteArray): ByteArray {
        return driverModel.evChargerParam?.run {
            val current = tripCurrent.toIntOrNull()?.let { it * 100 } ?: currentRange.first
            val currentBytes = current.toByteArray()
            Timber.tag(TAG).i("serialize currentBytes->${currentBytes.toHex()} $current")
            byteArray.replaceRangeValue(
                EVChargerPosition.SetTripCurrent,
                currentBytes
            )
        } ?: byteArray
    }
}