package com.ledvance.ble.constant

import java.util.UUID

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/10/25 14:03
 * Describe : Constants
 */
object Constants {
    internal val BLE_PREFIX: String = "DLM_"
    internal val FILTER_SERVICE_UUID: UUID = UUID.fromString("0000FFF0-0000-1000-8000-00805F9B34FB")
    internal val SERVICE_UUID: UUID = UUID.fromString("0000A002-0000-1000-8000-00805F9B34FB")
    internal val RX_CHAR_UUID: UUID = UUID.fromString("0000FFF3-0000-1000-8000-00805F9B34FB")
    internal val TX_CHAR_UUID: UUID = UUID.fromString("0000FFF4-0000-1000-8000-00805F9B34FB")
    internal val CLIENT_CHAR_CFG_UUID: UUID =
        UUID.fromString("00002902-0000-1000-8000-00805F9B34FB")

    internal const val START_FLAG: Byte = 0x68.toByte()
    internal const val MAX_DATA_LEN = 250
    internal const val DEFAULT_PART_SIZE = 200 // per doc default
    internal const val FRAME_INTERVAL_MS = 60L // ≥50ms, pick 60ms safety margin

    val tripCurrentRange = IntRange(1, 1000)
}