package com.ledvance.nfc.converter.serializer

import com.ledvance.nfc.converter.position.Position
import com.ledvance.nfc.data.model.NfcModel

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/5 09:37
 * Describe : IDriverSerializer
 */
internal interface INfcSerializer {
    val TAG
        get() = this::class.java.simpleName

    suspend fun serialize(model: NfcModel, byteArray: ByteArray): Pair<ByteArray, Position>
}