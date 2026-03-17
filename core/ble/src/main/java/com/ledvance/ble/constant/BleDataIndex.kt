package com.ledvance.ble.constant

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 10:39
 * Describe : BleDataIndex
 */
enum class BleDataIndex(val index: Int) {
    StartFlag(0),
    Length(1),
    Sequence(2),
    FrameType(3),
}