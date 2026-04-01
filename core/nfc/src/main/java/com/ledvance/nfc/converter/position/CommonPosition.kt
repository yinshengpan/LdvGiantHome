package com.ledvance.nfc.converter.position

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/6 13:45
 * Describe : CommonPosition
 */
internal object CommonPosition {
    data object CompanyCode : Position(0x00, 0x00)
    data object DeviceTypeCode : Position(0x04, 0x04)
    data object DeviceMacAddress : Position(0x08, 0x0D)
}