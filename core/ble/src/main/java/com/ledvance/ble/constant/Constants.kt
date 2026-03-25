package com.ledvance.ble.constant

import java.util.UUID

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/10/25 14:03
 * Describe : Constants
 */
object Constants {
    internal val BLE_PREFIX_GIANT: String = "HYD"
    internal val FILTER_SERVICE_UUID: UUID = UUID.fromString("00000000-0000-1000-8000-00805F9B34FB")
    internal val SERVICE_UUID_GIANT: UUID = UUID.fromString("0000FFF0-0000-1000-8000-00805F9B34FB")
    internal val WRITE_CHAR_UUID_GIANT: UUID = UUID.fromString("0000FFF3-0000-1000-8000-00805F9B34FB")
    internal val NOTIFY_CHAR_UUID_GIANT: UUID = UUID.fromString("0000FFF4-0000-1000-8000-00805F9B34FB")

    internal const val DEFAULT_PART_SIZE = 240
    internal const val FRAME_INTERVAL_MS = 60L // ≥50ms, pick 60ms safety margin
}