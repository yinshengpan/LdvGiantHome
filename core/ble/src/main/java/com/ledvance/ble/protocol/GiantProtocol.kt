package com.ledvance.ble.protocol

import com.ledvance.ble.core.BleClient
import com.ledvance.ble.core.CommandQueue
import com.ledvance.domain.bean.command.common.TimerDayOfWeek
import com.ledvance.domain.bean.command.common.TimerRepeat
import com.ledvance.domain.bean.command.common.TimerType
import com.ledvance.domain.bean.command.common.toGiantByte
import com.ledvance.domain.bean.command.giant.ColourType
import com.ledvance.domain.bean.command.giant.GiantCommandType
import com.ledvance.domain.bean.command.giant.GiantOnOff
import com.ledvance.domain.bean.command.common.ModeType
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
        client.write(buildCommand(GiantCommandType.QueryDeviceInfo.command, 0x01))
    }

    override suspend fun setBrightness(target: Int, brightness: Int) = queue.execute {
        Timber.tag(TAG).d("setBrightness: target=$target, brightness=$brightness")
        client.write(buildCommand(GiantCommandType.SetBrightness.command, target.toByte(), brightness.toByte()))
    }

    override suspend fun setSpeed(speed: Int) = queue.execute {
        Timber.tag(TAG).d("setSpeed: speed=$speed")
        client.write(buildCommand(GiantCommandType.SetSpeed.command, speed.toByte()))
    }

    override suspend fun setModeId(modeId: Int) = queue.execute {
        Timber.tag(TAG).d("setModeId: modeId=$modeId")
        client.write(buildCommand(GiantCommandType.SetModeOrScene.command, ModeType.GiantClassic.command, modeId.toByte()))
    }

    override suspend fun setModeType(modeType: Int) = queue.execute {
        Timber.tag(TAG).d("setModeType not supported by Bedside Lamp")
        false
    }

    override suspend fun setPower(power: Boolean, onOffType: Int) = queue.execute {
        Timber.tag(TAG).d("setPower: power=$power, onOffType=$onOffType")
        val stateByte = if (power) GiantOnOff.On.command else GiantOnOff.Off.command
        client.write(buildCommand(GiantCommandType.SetSwitch.command, onOffType.toByte(), stateByte))
    }

    override suspend fun setHs(h: Int, s: Int) = queue.execute {
        val rgb = ColorUtils.hsvToRgb(h, s, 100)
        Timber.tag(TAG).d("setHSV: h=$h, s=$s -> r:${rgb[0]},g:${rgb[1]},b:${rgb[2]}")
        client.write(
            buildCommand(
                GiantCommandType.SetColour.command,
                ColourType.RGB.command, // ColourType.RGB
                rgb[0].toByte(), rgb[1].toByte(), rgb[2].toByte()
            )
        )
    }

    override suspend fun setRgb(r: Int, g: Int, b: Int) = queue.execute {
        Timber.tag(TAG).d("setRgb: r=$r, g=$g, b=$b")
        client.write(
            data = buildCommand(
                cmd = GiantCommandType.SetColour.command,
                ColourType.RGB.command, // ColourType.RGB
                r.toByte(), g.toByte(), b.toByte()
            ),
            isDelay = false
        )
    }

    override suspend fun setCct(cct: Int) = queue.execute {
        Timber.tag(TAG).d("setCCT: cct=$cct")
        val (warm, cool) = ColorUtils.cctToWwCw(cct)
        client.write(
            buildCommand(
                GiantCommandType.SetColour.command,
                ColourType.WCT.command, // ColourType.WCT
                cool.toByte(), warm.toByte(), 0x00
            )
        )
    }

    override suspend fun setScene(sceneId: Int) = queue.execute {
        Timber.tag(TAG).d("setScene: sceneId=$sceneId")
        client.write(buildCommand(GiantCommandType.SetModeOrScene.command, ModeType.GiantScene.command, sceneId.toByte()))
    }

    override suspend fun setColor(type: Int, param1: Int, param2: Int, param3: Int) = queue.execute {
        Timber.tag(TAG).d("setColor: type=$type, params=[$param1, $param2, $param3]")
        client.write(buildCommand(GiantCommandType.SetColour.command, type.toByte(), param1.toByte(), param2.toByte(), param3.toByte()))
    }

    override suspend fun setMicRhythmEffect(effect: Int) = queue.execute {
        Timber.tag(TAG).d("setMicRhythmEffect: effect=$effect")
        client.write(buildCommand(GiantCommandType.SetMicRhythm.command, effect.toByte()))
    }

    override suspend fun setMicSensitivity(sensitivity: Int) = queue.execute {
        Timber.tag(TAG).d("setMicSensitivity: sensitivity=$sensitivity")
        client.write(buildCommand(GiantCommandType.SetMicSensitivity.command, sensitivity.toByte()))
    }

    override suspend fun setLedCount(count: Int) = queue.execute {
        Timber.tag(TAG).d("setLedCount: count=$count")
        val lowByte = (count and 0xFF).toByte()
        val highByte = ((count shr 8) and 0xFF).toByte()
        client.write(buildCommand(GiantCommandType.SetLedCount.command, lowByte, highByte))
    }

    override suspend fun setLineSequence(lineSequence: Int) = queue.execute {
        Timber.tag(TAG).d("setLineSequence: lineSequence=$lineSequence")
        client.write(buildCommand(GiantCommandType.SetWireOrder.command, lineSequence.toByte()))
    }

    override suspend fun setTimer(timerType: TimerType, hour: Int, min: Int, timerRepeat: TimerRepeat, delay: Int) = queue.execute {
        val weekCycleByte = timerRepeat.toGiantByte()
        val mode = timerType.mode
        val state = timerType.command
        Timber.tag(TAG).d("setTimer: timerType=$timerType, $hour:$min, cycle=${weekCycleByte.toInt().toBinary8()}")
        client.write(buildCommand(GiantCommandType.SetTimer.command, mode, state, hour.toByte(), min.toByte(), weekCycleByte))
    }

    override suspend fun queryTimer() = queue.execute {
        Timber.tag(TAG).d("queryTimer")
        client.write(buildCommand(GiantCommandType.GetTimingInfo.command, 0x01))
    }

    /** 设置设备当前时间 (Byte2=0x01, Byte3=时, Byte4=分, Byte5=秒, Byte6=星期) */
    override suspend fun setCurrentTime(hour: Int, min: Int, sec: Int, weekDay: Int) = queue.execute {
        Timber.tag(TAG).d("setCurrentTime: $hour:$min:$sec, weekDay=$weekDay")
        client.write(buildCommand(GiantCommandType.SetCurrentTime.command, 0x01, hour.toByte(), min.toByte(), sec.toByte(), weekDay.toByte()))
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
        client.write(buildCommand(GiantCommandType.QueryCurrentTime.command, 0x02))
    }

    override suspend fun resetDevice() = queue.execute {
        Timber.tag(TAG).d("resetDevice")
        client.write(buildCommand(GiantCommandType.DeviceReset.command, 0x2E))
    }
}