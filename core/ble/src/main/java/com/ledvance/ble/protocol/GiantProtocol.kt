package com.ledvance.ble.protocol

import com.ledvance.ble.core.BleClient
import com.ledvance.ble.core.CommandQueue

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:24
 * Describe : LedvanceProtocol
 */
class GiantProtocol(
    private val client: BleClient,
    private val queue: CommandQueue
) : BleProtocol {
    private val defByte = 0xFF.toByte()

    override suspend fun on() = queue.execute {
        client.write(byteArrayOf(0x0D, 0x04, 0x00, 0x0F, defByte, defByte, defByte, defByte, 0xD1.toByte()))
    }

    override suspend fun off() = queue.execute {
        client.write(byteArrayOf(0x0D, 0x04, 0x00, 0x00, defByte, defByte, defByte, defByte, 0xD1.toByte()))
    }

    override suspend fun setHSV(h: Int, s: Int, v: Int) = queue.execute {
        client.write(byteArrayOf(0x10, h.toByte(), s.toByte(), v.toByte()))
    }

    override suspend fun setCCT(temp: Int, brightness: Int) = queue.execute {
        client.write(byteArrayOf(0x20, temp.toByte(), brightness.toByte()))
    }
}