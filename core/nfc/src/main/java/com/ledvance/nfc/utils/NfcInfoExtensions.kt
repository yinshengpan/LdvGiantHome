package com.ledvance.nfc.utils

import com.ledvance.nfc.converter.mapping.Mapping
import com.ledvance.nfc.converter.position.BedsideLampPosition
import com.ledvance.nfc.converter.position.Position
import com.ledvance.nfc.data.model.NfcInfo
import com.ledvance.nfc.data.model.NfcModel
import com.ledvance.utils.extensions.subArray
import com.ledvance.utils.extensions.toInt
import java.nio.ByteOrder

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/6 17:47
 * Describe : NfcInfoExtensions
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

internal fun NfcInfo.getA2ContentRange(): Position {
    return when (mapping) {
        Mapping.BedsideLamp -> BedsideLampPosition.A2ContentRange
        else -> Position.Default
    }
}

internal fun NfcInfo.getA1A2BodyRange(): Position {
    return when (mapping) {
        Mapping.BedsideLamp -> BedsideLampPosition.A1A2BodyRange
        else -> Position.Default
    }
}

internal fun NfcInfo.getA3StartIndex(): Position {
    return when (mapping) {
        Mapping.BedsideLamp -> BedsideLampPosition.A3StartIndex
        else -> Position.Default
    }
}

internal fun NfcInfo.getA2StartIndex(): Position {
    return when (mapping) {
        Mapping.BedsideLamp -> BedsideLampPosition.A2StartIndex
        else -> Position.Default
    }
}

fun NfcModel.isValidCrc8() = crc8Check?.crc8 == crc8Check?.desiredCrc8
fun NfcModel.isValidFaultCodeCrc8() = crc8Check?.faultCodeCrc8 == crc8Check?.desiredFaultCodeCrc8