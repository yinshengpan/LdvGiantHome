package com.ledvance.nfc.data.model

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/8 16:32
 * Describe : NfcMode
 */
internal sealed interface NfcMode {
    data object Reader : NfcMode
    data object Writer : NfcMode
    data object Disable : NfcMode
}