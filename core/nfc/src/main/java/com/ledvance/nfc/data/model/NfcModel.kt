package com.ledvance.nfc.data.model

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/5 09:04
 * Describe : NfcModel
 */
data class NfcModel(
    val nfcInfo: NfcInfo,
    val crc8Check: Crc8Check? = null,
)