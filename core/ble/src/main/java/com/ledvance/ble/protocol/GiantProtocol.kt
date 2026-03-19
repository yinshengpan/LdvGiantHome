package com.ledvance.ble.protocol

import com.ledvance.ble.core.BleClient
import com.ledvance.ble.core.CommandQueue
import com.ledvance.domain.bean.command.BrightnessType
import com.ledvance.utils.ColorUtils

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
    private val endByte = 0xD1.toByte()
    private val headerByte = 0x1D.toByte()

    override suspend fun queryDeviceInfo() = queue.execute {
        client.write(byteArrayOf(headerByte, 0x00, 0x01, defByte, defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun setBrightness(target: BrightnessType, brightness: Int) = queue.execute {
        client.write(byteArrayOf(headerByte, 0x01, target.command, brightness.toByte(), defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun setSpeed(speed: Int) = queue.execute {
        client.write(byteArrayOf(headerByte, 0x02, speed.toByte(), defByte, defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun setMode(type: Int, modeId: Int) = queue.execute {
        client.write(byteArrayOf(headerByte, 0x03, type.toByte(), modeId.toByte(), defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun on(target: Int) = queue.execute {
        client.write(byteArrayOf(headerByte, 0x04, target.toByte(), 0x0F, defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun off(target: Int) = queue.execute {
        client.write(byteArrayOf(headerByte, 0x04, target.toByte(), 0x00, defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun setHSV(h: Int, s: Int) = queue.execute {
        val rgb = ColorUtils.hsvToRgb(h, s, 100)
        val r = rgb[0].toByte()
        val g = rgb[1].toByte()
        val b = rgb[2].toByte()
        client.write(byteArrayOf(headerByte, 0x05, 0x01, r, g, b, defByte, defByte, endByte))
    }

    override suspend fun setCCT(cct: Int) = queue.execute {
        val (warm,cool) = ColorUtils.cctToWwCw(cct)
        client.write(byteArrayOf(headerByte, 0x05, 0x04, cool.toByte(), warm.toByte(), 0x00, defByte, defByte, endByte))
    }

    override suspend fun setScene(sceneId: Byte) = queue.execute {
        client.write(byteArrayOf(headerByte, 0x03, 0x02, sceneId, defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun setColor(type: Int, param1: Int, param2: Int, param3: Int) = queue.execute {
        client.write(byteArrayOf(headerByte, 0x05, type.toByte(), param1.toByte(), param2.toByte(), param3.toByte(), defByte, defByte, endByte))
    }

    override suspend fun setMicRhythmEffect(effect: Int) = queue.execute {
        client.write(byteArrayOf(headerByte, 0x06, effect.toByte(), defByte, defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun setMicSensitivity(sensitivity: Int) = queue.execute {
        client.write(byteArrayOf(headerByte, 0x07, sensitivity.toByte(), defByte, defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun setLedCount(count: Int) = queue.execute {
        val lowByte = (count and 0xFF).toByte()
        val highByte = ((count shr 8) and 0xFF).toByte()
        client.write(byteArrayOf(headerByte, 0x08, lowByte, highByte, defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun setWireOrder(order: Int) = queue.execute {
        client.write(byteArrayOf(headerByte, 0x09, order.toByte(), defByte, defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun setTimer(switchState: Boolean, hour: Int, min: Int, weekCycle: Int) = queue.execute {
        val stateByte = if (switchState) 0x0F.toByte() else 0x00.toByte()
        client.write(byteArrayOf(headerByte, 0x0A, 0x01, stateByte, hour.toByte(), min.toByte(), weekCycle.toByte(), defByte, endByte))
    }

    override suspend fun queryTimer() = queue.execute {
        client.write(byteArrayOf(headerByte, 0x0A, 0x02, defByte, defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun setCurrentTime(hour: Int, min: Int, sec: Int, weekDay: Int) = queue.execute {
        client.write(byteArrayOf(headerByte, 0x0B, 0x01, hour.toByte(), min.toByte(), sec.toByte(), weekDay.toByte(), defByte, endByte))
    }

    override suspend fun queryCurrentTime() = queue.execute {
        client.write(byteArrayOf(headerByte, 0x0B, 0x02, defByte, defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun resetDevice() = queue.execute {
        client.write(byteArrayOf(headerByte, 0x0C, 0x2E, defByte, defByte, defByte, defByte, defByte, endByte))
    }

    override suspend fun getTimingInfo() = queue.execute {
        client.write(byteArrayOf(headerByte, 0x16, 0x01, defByte, defByte, defByte, defByte, defByte, endByte))
    }
}