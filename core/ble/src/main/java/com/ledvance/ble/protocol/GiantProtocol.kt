package com.ledvance.ble.protocol

import com.ledvance.ble.core.BleClient
import com.ledvance.ble.core.CommandQueue
import com.ledvance.domain.bean.TimerType
import com.ledvance.domain.bean.command.BrightnessType
import com.ledvance.domain.bean.command.ColourType
import com.ledvance.domain.bean.command.CommandType
import com.ledvance.domain.bean.command.ModeType
import com.ledvance.domain.bean.command.OnOff
import com.ledvance.domain.bean.command.OnOffType
import com.ledvance.domain.bean.command.timer.TimerDayOfWeek
import com.ledvance.utils.ColorUtils
import com.ledvance.utils.extensions.toTimeInfo
import java.time.LocalDateTime

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:24
 * Describe : GiantProtocol - 协议帧格式: [0x1D][CMD][Byte2..Byte7][0xD1]
 */
class GiantProtocol(
    private val client: BleClient,
    private val queue: CommandQueue
) : BleProtocol {

    companion object {
        const val HEADER_BYTE = 0x1D.toByte()
        const val END_BYTE = 0xD1.toByte()
        private const val DEFAULT_BYTE = 0xFF.toByte()
    }

    private fun buildCommand(cmd: Byte, vararg params: Byte): ByteArray {
        val bytes = ByteArray(9) { DEFAULT_BYTE }
        bytes[0] = HEADER_BYTE
        bytes[1] = cmd
        params.forEachIndexed { index, byte ->
            if (index < 6) {
                bytes[index + 2] = byte
            }
        }
        bytes[8] = END_BYTE
        return bytes
    }

    override suspend fun queryDeviceInfo() = queue.execute {
        client.write(buildCommand(CommandType.QueryDeviceInfo.command, 0x01))
    }

    override suspend fun setBrightness(target: BrightnessType, brightness: Int) = queue.execute {
        client.write(buildCommand(CommandType.SetBrightness.command, target.command, brightness.toByte()))
    }

    override suspend fun setSpeed(speed: Int) = queue.execute {
        client.write(buildCommand(CommandType.SetSpeed.command, speed.toByte()))
    }

    override suspend fun setMode(modeType: ModeType, modeId: Int) = queue.execute {
        client.write(buildCommand(CommandType.SetModeOrScene.command, modeType.command, modeId.toByte()))
    }

    override suspend fun setPower(power: Boolean, onOffType: OnOffType) = queue.execute {
        val stateByte = if (power) OnOff.On.command else OnOff.Off.command
        client.write(buildCommand(CommandType.SetSwitch.command, onOffType.command, stateByte))
    }

    override suspend fun setHSV(h: Int, s: Int) = queue.execute {
        val rgb = ColorUtils.hsvToRgb(h, s, 100)
        client.write(buildCommand(
            CommandType.SetColour.command,
            ColourType.RGB.command,
            rgb[0].toByte(), rgb[1].toByte(), rgb[2].toByte()
        ))
    }

    override suspend fun setCCT(cct: Int) = queue.execute {
        val (warm, cool) = ColorUtils.cctToWwCw(cct)
        client.write(buildCommand(
            CommandType.SetColour.command,
            ColourType.WCT.command,
            cool.toByte(), warm.toByte(), 0x00
        ))
    }

    override suspend fun setScene(sceneId: Byte) = queue.execute {
        client.write(buildCommand(CommandType.SetModeOrScene.command, ModeType.Scene.command, sceneId))
    }

    override suspend fun setColor(type: ColourType, param1: Int, param2: Int, param3: Int) = queue.execute {
        client.write(buildCommand(CommandType.SetColour.command, type.command, param1.toByte(), param2.toByte(), param3.toByte()))
    }

    override suspend fun setMicRhythmEffect(effect: Int) = queue.execute {
        client.write(buildCommand(CommandType.SetMicRhythm.command, effect.toByte()))
    }

    override suspend fun setMicSensitivity(sensitivity: Int) = queue.execute {
        client.write(buildCommand(CommandType.SetMicSensitivity.command, sensitivity.toByte()))
    }

    override suspend fun setLedCount(count: Int) = queue.execute {
        val lowByte = (count and 0xFF).toByte()
        val highByte = ((count shr 8) and 0xFF).toByte()
        client.write(buildCommand(CommandType.SetLedCount.command, lowByte, highByte))
    }

    override suspend fun setWireOrder(order: Int) = queue.execute {
        client.write(buildCommand(CommandType.SetWireOrder.command, order.toByte()))
    }

    override suspend fun setTimer(timerType: TimerType, hour: Int, min: Int, weekCycle: Int) = queue.execute {
        val state = if (timerType == TimerType.ON) OnOff.On.command else OnOff.Off.command
        client.write(buildCommand(CommandType.SetTimer.command, 0x01, state, hour.toByte(), min.toByte(), weekCycle.toByte()))
    }

    override suspend fun queryTimer() = queue.execute {
        client.write(buildCommand(CommandType.GetTimingInfo.command, 0x01))
    }

    /** 设置设备当前时间 (Byte2=0x01, Byte3=时, Byte4=分, Byte5=秒, Byte6=星期) */
    override suspend fun setCurrentTime(hour: Int, min: Int, sec: Int, weekDay: Int) = queue.execute {
        client.write(buildCommand(CommandType.SetCurrentTime.command, 0x01, hour.toByte(), min.toByte(), sec.toByte(), weekDay.toByte()))
    }

    override suspend fun syncCurrentTime() {
        val timeInfo = LocalDateTime.now().toTimeInfo()
        setCurrentTime(
            hour = timeInfo.hour,
            min = timeInfo.minute,
            sec = timeInfo.second,
            weekDay = TimerDayOfWeek.formDayOfWeek(timeInfo.week).command.toInt()
        )
    }

    /** 查询设备当前时间 (Byte2=0x02) */
    override suspend fun queryCurrentTime() = queue.execute {
        client.write(buildCommand(CommandType.QueryCurrentTime.command, 0x02))
    }

    override suspend fun resetDevice() = queue.execute {
        client.write(buildCommand(CommandType.DeviceReset.command, 0x2E))
    }
}