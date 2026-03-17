package com.ledvance.utils.extensions

import android.util.Base64
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/5 08:41
 * Describe : ByteExtensions
 */
fun ByteArray.toHexLn(): String {
    var count = 0
    return joinToString(separator = "") { eachByte ->
        ++count
        val isFirstIndex = count % 4 == 1
        val prefix = if (isFirstIndex && size > 4) "\nLine ${count / 4}:" else ""
        "$prefix%02x ".format(eachByte)
    }
}

fun ByteArray.toHex(): String = joinToString(separator = "") { eachByte ->
    "%02x ".format(eachByte)
}

fun Byte.toHex(): String = "%02x".format(this)

/** Convert signed Byte (-128~127) to unsigned Int (0~255) */
fun Byte.toUnsignedInt(): Int = toInt() and 0xFF

@OptIn(ExperimentalStdlibApi::class)
fun Short.toHex(): String = toHexString()

fun Short.toByteArray(order: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
    return ByteBuffer.allocate(Short.SIZE_BYTES)
        .order(order).putShort(this).array()
}

fun Long.toByteArray(
    capacity: Int = Long.SIZE_BYTES,
    order: ByteOrder = ByteOrder.BIG_ENDIAN
): ByteArray {
    return ByteBuffer.allocate(capacity)
        .order(order)
        .putLong(this)
        .array()
}

fun ByteArray.bytesToShort(): Short {
    return ByteBuffer.wrap(this).short
}

fun ByteArray.replaceRangeValue(startIndex: Int, replacement: ByteArray): ByteArray {
    if (startIndex < 0 || (startIndex + replacement.size) > this.size) {
        return byteArrayOf()
    }
    System.arraycopy(replacement, 0, this, startIndex, replacement.size)
    return this
}

fun ByteArray.toLongLittleEndian(): Long {
    return toLong(order = ByteOrder.LITTLE_ENDIAN)
}

fun ByteArray.toIntLittleEndian(): Int {
    return toInt(order = ByteOrder.LITTLE_ENDIAN)
}

fun ByteArray.toIntBigEndian(): Int {
    return toInt()
}

fun ByteArray.toLongBigEndian(): Long {
    return toLong()
}

fun ByteArray.toInt(order: ByteOrder = ByteOrder.BIG_ENDIAN): Int {
    if (this.size !in 1..4) return -1
    val buffer = ByteBuffer.allocate(4)
    buffer.order(order)
    if (order == ByteOrder.BIG_ENDIAN) {
        buffer.put(ByteArray(4 - size)) // 高位补零
        buffer.put(this)
    } else {
        buffer.put(this)
        buffer.put(ByteArray(4 - size)) // 低位补零
    }
    buffer.flip()
    return buffer.int
}

fun ByteArray.toLong(order: ByteOrder = ByteOrder.BIG_ENDIAN): Long {
    require(size in 1..8) { "Byte array size must be between 1 and 8" }
    var result = 0L
    for (i in indices) {
        val shift = if (order == ByteOrder.BIG_ENDIAN) {
            (8 * (size - 1 - i))
        } else {
            (8 * i)
        }
        result = result or ((this[i].toLong() and 0xFF) shl shift)
    }
    return result
}

fun ByteArray.subArray(startIndex: Int, endIndex: Int): ByteArray {
    if (startIndex !in indices || endIndex !in indices || startIndex > endIndex) {
        return byteArrayOf()
    }
    if (startIndex == endIndex) {
        return byteArrayOf(this[startIndex])
    }
    val length = endIndex - startIndex + 1
    val newArray = ByteArray(length)
    System.arraycopy(this, startIndex, newArray, 0, length)
    return newArray
}

fun String.toByteArray(s: String): ByteArray {
    val len = s.length
    val data = ByteArray(len / 2)
    var i = 0
    while (i < len) {
        data[i / 2] = ((Character.digit(s[i], 16) shl 4)
                + Character.digit(s[i + 1], 16)).toByte()
        i += 2
    }
    return data
}

fun ByteArray.toBase64(): String {
    return Base64.encodeToString(this, Base64.NO_WRAP)
}

fun String.base64ToByteArray(): ByteArray {
    return tryCatchReturn { Base64.decode(this, Base64.NO_WRAP) } ?: byteArrayOf()
}


fun Int.toShortByteArray(): ByteArray {
    return this.toShort().toSingleByteArray()
}

fun Int.toByteArray(order: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
    return ByteBuffer.allocate(Int.SIZE_BYTES).order(order).putInt(this).array()
}

fun Short.toSingleByteArray(order: ByteOrder = ByteOrder.BIG_ENDIAN): ByteArray {
    return ByteBuffer.allocate(Short.SIZE_BYTES)
        .order(order).putShort(this).array()
}

fun Int.toSingleByteArray(): ByteArray {
    return byteArrayOf(this.toUByte().toByte())
}


fun Byte.asInt(order: ByteOrder = ByteOrder.BIG_ENDIAN): Int {
    return byteArrayOf(this).toInt(order)
}