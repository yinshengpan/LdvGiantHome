package com.ledvance.nfc.utils

import com.ledvance.nfc.converter.mapping.Mapping
import com.ledvance.nfc.converter.position.EVChargerPosition
import com.ledvance.nfc.converter.position.Position
import com.ledvance.nfc.data.model.DriverInfo
import com.ledvance.nfc.data.model.DriverModel
import com.ledvance.utils.extensions.subArray
import com.ledvance.utils.extensions.toInt
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/6 17:47
 * Describe : DriverInfoExtensions
 */

internal fun ByteArray.subArray(position: Position): ByteArray {
    return subArray(position.startIndex, position.endIndex)
}

internal fun ByteArray.replaceRangeValue(position: Position, replacement: ByteArray): ByteArray {
    val startIndex = position.startIndex
    if (startIndex < 0 || (startIndex + replacement.size) > this.size) {
        return byteArrayOf()
    }
    System.arraycopy(replacement, 0, this, startIndex, replacement.size)
    return this
}

internal fun ByteArray.getInt(position: Position, order: ByteOrder = ByteOrder.BIG_ENDIAN): Int {
    return subArray(position.startIndex, position.endIndex).toInt(order)
}

internal fun DriverInfo.getA2ContentRange(): Position {
    return when (mapping) {
        Mapping.Charger.version -> EVChargerPosition.A2ContentRange
        else -> Position.Default
    }
}

internal fun DriverInfo.getA1A2BodyRange(): Position {
    return when (mapping) {
        Mapping.Charger.version -> EVChargerPosition.A1A2BodyRange
        else -> Position.Default
    }
}

internal fun DriverInfo.getA3StartIndex(): Position {
    return when (mapping) {
        Mapping.Charger.version -> EVChargerPosition.A3StartIndex
        else -> Position.Default
    }
}

internal fun DriverInfo.getA2StartIndex(): Position {
    return when (mapping) {
        Mapping.Charger.version -> EVChargerPosition.A2StartIndex
        else -> Position.Default
    }
}

fun DriverModel.isValidCrc8() = crc8Check?.crc8 == crc8Check?.desiredCrc8
fun DriverModel.isValidFaultCodeCrc8() = crc8Check?.faultCodeCrc8 == crc8Check?.desiredFaultCodeCrc8