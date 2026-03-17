package com.ledvance.nfc.converter.position

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/6 13:45
 * Describe : CommonPosition
 */
internal object CommonPosition {
    data object CompanyCode : Position(0x00, 0x00)
    data object DriverTypeCode : Position(0x04, 0x04)
    data object DeviceSNCode : Position(0x10, 0x13)
}