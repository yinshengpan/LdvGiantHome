package com.ledvance.nfc.converter.position

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/6 13:45
 * Describe : CommonPosition
 */
internal object CommonPosition {
    data object CompanyCode : Position(0x10, 0x10)
    data object DeviceTypeCode : Position(0x14, 0x14)
    data object DeviceMacAddress : Position(0x18, 0x1D)
}