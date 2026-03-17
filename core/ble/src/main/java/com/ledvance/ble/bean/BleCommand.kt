package com.ledvance.ble.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 10:05
 * Describe : BleCommandResult
 */
enum class BleCommand(val requestFlag: Int, val responseFlag: Int) {
    Handshake(0x01, 0x02),
    EditChargingPile(0x50, 0x51),
    GetChargerPile(0x52, 0x53),
    SetConfiguration(0x54, 0x55),
    GetConfiguration(0x58, 0x59),
    FileInfo(0x70, 0x71),
    File(0x72, 0x73),
}