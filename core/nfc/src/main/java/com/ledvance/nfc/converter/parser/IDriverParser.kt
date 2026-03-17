package com.ledvance.nfc.converter.parser

import com.ledvance.nfc.data.model.DriverInfo
import com.ledvance.nfc.data.model.DriverModel

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/5 09:37
 * Describe : IDriverParser
 */
internal interface IDriverParser {
    suspend fun parse(driverInfo: DriverInfo, byteArray: ByteArray): DriverModel
}