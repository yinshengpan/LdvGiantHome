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
    internal val BLE_PREFIX_LIST: List<String> = listOf(BLE_PREFIX_GIANT, "LEDVANCE Bedside lamp")

    internal const val DEFAULT_PART_SIZE = 240
    internal const val FRAME_INTERVAL_MS = 60L // ≥50ms, pick 60ms safety margin
}