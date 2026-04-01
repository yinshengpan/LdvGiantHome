package com.ledvance.nfc.converter.parser

import com.ledvance.nfc.data.model.NfcInfo
import com.ledvance.nfc.data.model.NfcModel

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/5 09:37
 * Describe : IDriverParser
 */
internal interface INfcParser {
    suspend fun parse(nfcInfo: NfcInfo, byteArray: ByteArray): NfcModel
}