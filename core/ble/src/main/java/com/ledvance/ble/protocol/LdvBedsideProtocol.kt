package com.ledvance.ble.protocol

// import removed
import com.ledvance.ble.core.BleClient
import com.ledvance.ble.core.CommandQueue
import com.ledvance.domain.bean.command.common.TimerRepeat
import com.ledvance.domain.bean.command.common.TimerType
import com.ledvance.domain.bean.command.common.toLdvByte
import com.ledvance.domain.bean.command.ldv.LdvCommandType
import com.ledvance.domain.bean.command.ldv.LdvModeType
import com.ledvance.domain.bean.command.ldv.LdvOnOff
import timber.log.Timber
import java.time.LocalDateTime

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:24
 * Describe : LdvBedsideProtocol
 */
class LdvBedsideProtocol(
    private val client: BleClient,
    private val queue: CommandQueue
) : BleProtocol {

    private val TAG = "LdvBedsideProtocol"

    companion object {
        const val HEADER_BYTE = 0x5A.toByte()
    }

    private fun buildCommand(cmd: Byte, vararg params: Byte): ByteArray {
        val dataLen = params.size
        val bytes = ByteArray(3 + dataLen)
        bytes[0] = HEADER_BYTE
        bytes[1] = cmd
        bytes[2] = dataLen.toByte()
        params.forEachIndexed { index, byte ->
            bytes[3 + index] = byte
        }
        return bytes
    }

    override suspend fun queryDeviceInfo() = queue.execute {
        Timber.tag(TAG).d("queryDeviceInfo: using GetStatus(0x05)")
        client.write(buildCommand(LdvCommandType.QueryDeviceInfo.command, 0x01.toByte()))
    }

    override suspend fun setBrightness(target: Int, brightness: Int) = queue.execute {
        val protocolValue = (brightness * 50 / 100).coerceIn(1, 50)
        Timber.tag(TAG).d("setBrightness: UI=$brightness mapped to Protocol=$protocolValue")
        client.write(buildCommand(LdvCommandType.SetBrightness.command, protocolValue.toByte()))
    }

    override suspend fun setSpeed(speed: Int) = queue.execute {
        Timber.tag(TAG).d("setSpeed not supported by Bedside Lamp")
        false
    }

    override suspend fun setModeId(modeId: Int) = queue.execute {
        Timber.tag(TAG).d("setModeId not supported by Bedside Lamp")
        false
    }

    override suspend fun setModeType(modeType: Int) = queue.execute {
        Timber.tag(TAG).d("setModeType: modeType=$modeType")
        client.write(buildCommand(LdvCommandType.SetMode.command, modeType.toByte()))
    }

    override suspend fun setPower(power: Boolean, onOffType: Int) = queue.execute {
        Timber.tag(TAG).d("setPower: power=$power")
        val stateByte = if (power) LdvOnOff.On.command else LdvOnOff.Off.command
        client.write(buildCommand(LdvCommandType.SetPower.command, stateByte))
    }

    override suspend fun setHs(h: Int, s: Int) = queue.execute {
        Timber.tag(TAG).d("setHs not supported by Bedside Lamp")
        false
    }

    override suspend fun setRgb(r: Int, g: Int, b: Int) = queue.execute {
        Timber.tag(TAG).d("setRgb not supported by Bedside Lamp")
        false
    }

    override suspend fun setCct(cct: Int) = queue.execute {
        Timber.tag(TAG).d("setCCT: originalCct=$cct")
        val value = cct.coerceIn(0, 100)
        // 0~100 → 1800~6500
        val realCct = 1800 + (value * (6500 - 1800) / 100)
        Timber.tag(TAG).d("setCCT: realCct=$realCct")
        val cctL = (realCct and 0xFF).toByte()         // 低位
        val cctH = ((realCct shr 8) and 0xFF).toByte() // 高位
        Timber.tag(TAG).d("setCCT: L=$cctL, H=$cctH")
        client.write(buildCommand(LdvCommandType.SetCct.command, cctL, cctH))
    }

    override suspend fun setScene(sceneId: Int) = queue.execute {
        Timber.tag(TAG).d("setScene not supported by Bedside Lamp")
        false
    }

    override suspend fun setColor(type: Int, param1: Int, param2: Int, param3: Int) = queue.execute {
        Timber.tag(TAG).d("setColor not supported by Bedside Lamp")
        false
    }

    override suspend fun setMicRhythmEffect(effect: Int) = queue.execute {
        Timber.tag(TAG).d("setMicRhythmEffect not supported by Bedside Lamp")
        false
    }

    override suspend fun setMicSensitivity(sensitivity: Int) = queue.execute {
        Timber.tag(TAG).d("setMicSensitivity not supported by Bedside Lamp")
        false
    }

    override suspend fun setLedCount(count: Int) = queue.execute {
        Timber.tag(TAG).d("setLedCount not supported by Bedside Lamp")
        false
    }

    override suspend fun setLineSequence(lineSequence: Int) = queue.execute {
        Timber.tag(TAG).d("setLineSequence not supported by Bedside Lamp")
        false
    }

    override suspend fun setTimer(timerType: TimerType, hour: Int, min: Int, timerRepeat: TimerRepeat, duration: Int) = queue.execute {
        Timber.tag(TAG).d("setTimer: timerType=$timerType, $hour:$min, repeat=$timerRepeat, duration=$duration")
        val idxByte = timerType.command
        val modeByte = timerType.mode
        val ldvWeekByte = timerRepeat.toLdvByte()
        val enableByte = if (timerRepeat.enabled) LdvOnOff.On.command else LdvOnOff.Off.command
        client.write(
            data = buildCommand(
                cmd = LdvCommandType.SetTimer.command,
                idxByte, hour.toByte(), min.toByte(), duration.toByte(), modeByte, ldvWeekByte, enableByte
            )
        )
    }

    override suspend fun queryTimer() = queue.execute {
        Timber.tag(TAG).d("queryTimer")
        client.write(buildCommand(LdvCommandType.QueryTimer.command, LdvModeType.Wakeup.command))
        client.write(buildCommand(LdvCommandType.QueryTimer.command, LdvModeType.AlwaysOn.command))
    }

    override suspend fun setCurrentTime(hour: Int, min: Int, sec: Int, weekDay: Int) = queue.execute {
        val now = LocalDateTime.now()
        val year = now.year
        val yearL = (year and 0xFF).toByte()
        val yearH = ((year shr 8) and 0xFF).toByte()
        val month = now.monthValue.toByte()
        val day = now.dayOfMonth.toByte()
        
        // 0=Sun, 1=Mon, ..., 6=Sat
        val ldvWeekDay = (if (weekDay == 7) 0 else weekDay).toByte()
        Timber.tag(TAG).d("setCurrentTime: $year-$month-$day $hour:$min:$sec weekDay=$ldvWeekDay")
        client.write(buildCommand(LdvCommandType.TimeSync.command, yearL, yearH, month, day, hour.toByte(), min.toByte(), sec.toByte(), ldvWeekDay))
    }

    override suspend fun syncCurrentTime(): Boolean {
        val now = LocalDateTime.now()
        // week value from 1(Mon) to 7(Sun), Android ISO-8601
        return setCurrentTime(now.hour, now.minute, now.second, now.dayOfWeek.value)
    }

    override suspend fun queryCurrentTime() = queue.execute {
        Timber.tag(TAG).d("queryCurrentTime not supported by Bedside Lamp")
        false
    }

    override suspend fun resetDevice() = queue.execute {
        Timber.tag(TAG).d("resetDevice not supported by Bedside Lamp")
        false
    }
}