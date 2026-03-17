package com.ledvance.nfc.converter.serializer

import com.ledvance.nfc.converter.position.EVChargerPosition
import com.ledvance.nfc.converter.position.Position
import com.ledvance.nfc.converter.parser.feature.Feature
import com.ledvance.nfc.data.model.DriverModel
import com.ledvance.nfc.utils.crc8.EVChargerCrc8
import com.ledvance.nfc.utils.replaceRangeValue
import com.ledvance.utils.extensions.toHex
import com.ledvance.utils.extensions.toSingleByteArray
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 14:42
 * Describe : EVChargerSerializer
 */
internal class EVChargerSerializer : IDriverSerializer {
    override suspend fun serialize(
        model: DriverModel,
        byteArray: ByteArray
    ): Pair<ByteArray, Position> = withContext(Dispatchers.IO) {
        val serialized = FeatureConverter.FeatureSerializerChain(byteArray, model)
            .apply(Feature.EVChargerDriverParam)
            .result()
        val newByteArray = serialized.replaceCrc8()
        return@withContext newByteArray to EVChargerPosition.A3StartIndex
    }


    private fun ByteArray.replaceCrc8(): ByteArray {
        val crc8 = EVChargerCrc8.calculationDataCrc8(this)
        val crc8ByteArray = crc8.toSingleByteArray()
        Timber.tag(TAG).i("replaceCrc: crc8:$crc8 byte:${crc8ByteArray.toHex()}")
        return this.replaceRangeValue(EVChargerPosition.Crc8, crc8ByteArray)
    }
}