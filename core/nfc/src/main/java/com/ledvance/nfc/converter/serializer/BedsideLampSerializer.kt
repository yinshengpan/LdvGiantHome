package com.ledvance.nfc.converter.serializer

import com.ledvance.nfc.converter.position.BedsideLampPosition
import com.ledvance.nfc.converter.position.Position
import com.ledvance.nfc.data.model.NfcModel
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
 * Describe : BedsideLampSerializer
 */
internal class BedsideLampSerializer : INfcSerializer {
    override suspend fun serialize(
        model: NfcModel,
        byteArray: ByteArray
    ): Pair<ByteArray, Position> = withContext(Dispatchers.IO) {
        val serialized = FeatureConverter.FeatureSerializerChain(byteArray, model)
            .result()
        val newByteArray = serialized.replaceCrc8()
        return@withContext newByteArray to BedsideLampPosition.A3StartIndex
    }


    private fun ByteArray.replaceCrc8(): ByteArray {
        val crc8 = 0
        val crc8ByteArray = crc8.toSingleByteArray()
        Timber.tag(TAG).i("replaceCrc: crc8:$crc8 byte:${crc8ByteArray.toHex()}")
        return this.replaceRangeValue(BedsideLampPosition.Crc8, crc8ByteArray)
    }
}