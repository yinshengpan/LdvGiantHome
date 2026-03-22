package com.ledvance.ble.protocol

import com.ledvance.ble.core.BleClient
import com.ledvance.ble.core.CommandQueue
import com.ledvance.domain.bean.TimerType
import com.ledvance.domain.bean.command.BrightnessType
import com.ledvance.domain.bean.command.ColourType
import com.ledvance.domain.bean.command.CommandType
import com.ledvance.domain.bean.command.LineSequence
import com.ledvance.domain.bean.command.ModeId
import com.ledvance.domain.bean.command.ModeType
import com.ledvance.domain.bean.command.OnOff
import com.ledvance.domain.bean.command.OnOffType
import com.ledvance.domain.bean.command.scenes.Scene
import com.ledvance.domain.bean.command.timer.TimerDayOfWeek
import com.ledvance.utils.ColorUtils
import com.ledvance.utils.extensions.toBinary8
import com.ledvance.utils.extensions.toTimeInfo
import timber.log.Timber
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

    private val TAG = "GiantProtocol"

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
        Timber.tag(TAG).d("queryDeviceInfo")
        client.write(buildCommand(CommandType.QueryDeviceInfo.command, 0x01))
    }

    override suspend fun setBrightness(target: BrightnessType, brightness: Int) = queue.execute {
        Timber.tag(TAG).d("setBrightness: target=$target, brightness=$brightness")
        client.write(buildCommand(CommandType.SetBrightness.command, target.command, brightness.toByte()))
    }

    override suspend fun setSpeed(speed: Int) = queue.execute {
        Timber.tag(TAG).d("setSpeed: speed=$speed")
        client.write(buildCommand(CommandType.SetSpeed.command, speed.toByte()))
    }

    override suspend fun setMode(modeId: ModeId) = queue.execute {
        Timber.tag(TAG).d("setMode: modeId=$modeId")
        client.write(buildCommand(CommandType.SetModeOrScene.command, ModeType.Classic.command, modeId.command))
    }

    override suspend fun setPower(power: Boolean, onOffType: OnOffType) = queue.execute {
        Timber.tag(TAG).d("setPower: power=$power, onOffType=$onOffType")
        val stateByte = if (power) OnOff.On.command else OnOff.Off.command
        client.write(buildCommand(CommandType.SetSwitch.command, onOffType.command, stateByte))
    }

    override suspend fun setHSV(h: Int, s: Int) = queue.execute {
        Timber.tag(TAG).d("setHSV: h=$h, s=$s")
        val rgb = ColorUtils.hsvToRgb(h, s, 100)
        client.write(
            buildCommand(
                CommandType.SetColour.command,
                ColourType.RGB.command,
                rgb[0].toByte(), rgb[1].toByte(), rgb[2].toByte()
            )
        )
    }

    override suspend fun setCCT(cct: Int) = queue.execute {
        Timber.tag(TAG).d("setCCT: cct=$cct")
        val (warm, cool) = ColorUtils.cctToWwCw(cct)
        client.write(
            buildCommand(
                CommandType.SetColour.command,
                ColourType.WCT.command,
                cool.toByte(), warm.toByte(), 0x00
            )
        )
    }

    override suspend fun setScene(sceneId: Scene) = queue.execute {
        Timber.tag(TAG).d("setScene: sceneId=$sceneId")
        client.write(buildCommand(CommandType.SetModeOrScene.command, ModeType.Scene.command, sceneId.command))
    }

    override suspend fun setColor(type: ColourType, param1: Int, param2: Int, param3: Int) = queue.execute {
        Timber.tag(TAG).d("setColor: type=$type, params=[$param1, $param2, $param3]")
        client.write(buildCommand(CommandType.SetColour.command, type.command, param1.toByte(), param2.toByte(), param3.toByte()))
    }

    override suspend fun setMicRhythmEffect(effect: Int) = queue.execute {
        Timber.tag(TAG).d("setMicRhythmEffect: effect=$effect")
        client.write(buildCommand(CommandType.SetMicRhythm.command, effect.toByte()))
    }

    override suspend fun setMicSensitivity(sensitivity: Int) = queue.execute {
        Timber.tag(TAG).d("setMicSensitivity: sensitivity=$sensitivity")
        client.write(buildCommand(CommandType.SetMicSensitivity.command, sensitivity.toByte()))
    }

    override suspend fun setLedCount(count: Int) = queue.execute {
        Timber.tag(TAG).d("setLedCount: count=$count")
        val lowByte = (count and 0xFF).toByte()
        val highByte = ((count shr 8) and 0xFF).toByte()
        client.write(buildCommand(CommandType.SetLedCount.command, lowByte, highByte))
    }

    override suspend fun setLineSequence(lineSequence: LineSequence) = queue.execute {
        Timber.tag(TAG).d("setLineSequence: lineSequence=$lineSequence")
        client.write(buildCommand(CommandType.SetWireOrder.command, lineSequence.command))
    }

    override suspend fun setTimer(timerType: TimerType, hour: Int, min: Int, weekCycle: Int) = queue.execute {
        Timber.tag(TAG).d("setTimer: timerType=$timerType, $hour:$min, cycle=${weekCycle.toBinary8()}")
        val state = if (timerType == TimerType.ON) OnOff.On.command else OnOff.Off.command
        client.write(buildCommand(CommandType.SetTimer.command, 0x01, state, hour.toByte(), min.toByte(), weekCycle.toByte()))
    }

    override suspend fun queryTimer() = queue.execute {
        Timber.tag(TAG).d("queryTimer")
        client.write(buildCommand(CommandType.GetTimingInfo.command, 0x01))
    }

    /** 设置设备当前时间 (Byte2=0x01, Byte3=时, Byte4=分, Byte5=秒, Byte6=星期) */
    override suspend fun setCurrentTime(hour: Int, min: Int, sec: Int, weekDay: Int) = queue.execute {
        Timber.tag(TAG).d("setCurrentTime: $hour:$min:$sec, weekDay=$weekDay")
        client.write(buildCommand(CommandType.SetCurrentTime.command, 0x01, hour.toByte(), min.toByte(), sec.toByte(), weekDay.toByte()))
    }

    override suspend fun syncCurrentTime(): Boolean {
        val timeInfo = LocalDateTime.now().toTimeInfo()
        Timber.tag(TAG).d("syncCurrentTime: $timeInfo")
        return setCurrentTime(
            hour = timeInfo.hour,
            min = timeInfo.minute,
            sec = timeInfo.second,
            weekDay = TimerDayOfWeek.formDayOfWeek(timeInfo.week).command.toInt()
        )
    }

    /** 查询设备当前时间 (Byte2=0x02) */
    override suspend fun queryCurrentTime() = queue.execute {
        Timber.tag(TAG).d("queryCurrentTime")
        client.write(buildCommand(CommandType.QueryCurrentTime.command, 0x02))
    }

    override suspend fun resetDevice() = queue.execute {
        Timber.tag(TAG).d("resetDevice")
        client.write(buildCommand(CommandType.DeviceReset.command, 0x2E))
    }
}