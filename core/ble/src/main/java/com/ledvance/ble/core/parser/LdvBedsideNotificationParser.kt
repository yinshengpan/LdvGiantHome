package com.ledvance.ble.core.parser

import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceTimer
import com.ledvance.domain.bean.command.NotifyType
import com.ledvance.domain.bean.command.common.TimerIndex
import com.ledvance.domain.bean.command.common.toTimerType
import com.ledvance.domain.bean.command.ldv.LdvOnOff
import com.ledvance.domain.bean.command.ldv.LdvReportType
import com.ledvance.utils.extensions.toUnsignedInt
import timber.log.Timber

class LdvBedsideNotificationParser(
    private val registry: DeviceRegistry
) : BleNotificationParser {

    override val TAG = "LdvBedsideNotification"

    override fun parse(deviceId: DeviceId, bytes: ByteArray) {
        // [0x5B] [cmd] [data_len] [data1 ... datan]
        if (bytes.first() != NotifyType.LdvResponse.command) return
        if (bytes.size < 4) return

        val cmd = bytes[1]
        val dataLen = bytes[2].toUnsignedInt()
        if (bytes.size != 3 + dataLen) return // Incomplete frame

        when (cmd) {
            LdvReportType.DeviceStatus.command -> parseLdvBedsideDeviceState(deviceId, bytes)
            LdvReportType.TimerStatus.command -> parseLdvBedsideTimingInfo(deviceId, bytes)
        }
    }

    private fun parseLdvBedsideDeviceState(deviceId: DeviceId, bytes: ByteArray) {
        // data: 开关(3) + 亮度(4) + 色温L(5) + 色温H(6) + 模式(7)
        if (bytes.size < 8) return

        val power = bytes[3] == LdvOnOff.On.command
        if (!power) {
            Timber.tag(TAG).i("parseLdvBedsideDeviceState: $deviceId -> Power=$power")
            registry.updateDeviceState(deviceId, power)
            return
        }
        val protocolBrightness = bytes[4].toUnsignedInt()
        val brightness = (protocolBrightness * 100 / 50).coerceIn(1, 100)

        // 色温拼接 (低字节在前，高字节在后)
        val cctL = bytes[5].toUnsignedInt()
        val cctH = bytes[6].toUnsignedInt()
        val cct = (cctH shl 8) or cctL

        val modeTypeValue = bytes[7].toUnsignedInt()

        Timber.tag(TAG).i("parseLdvBedsideDeviceState: $deviceId -> Power=$power, BR=$brightness, CCT=$cct, modeTypeValue=$modeTypeValue")

        // Registry 期望所有基础值，由于床头灯不支持 RGBW 与 speed，利用 registry 保留兜底
        val old = registry.get(deviceId)
        registry.updateDeviceInfo(
            deviceId = deviceId,
            power = power,
            r = old?.r ?: 0,
            g = old?.g ?: 0,
            b = old?.b ?: 0,
            w = cct.coerceIn(1000, 6500),
            brightness = brightness,
            modeType = modeTypeValue,
            modeId = 0,
            speed = old?.speed ?: 0
        )
    }

    private fun parseLdvBedsideTimingInfo(deviceId: DeviceId, bytes: ByteArray) {
        // [0x5B] [0x21] [0x07] [index(3)] [startHour(4)] [startMinute(5)] [duration(6)] [mode(7)] [week(8)] [enabled(9)]
        if (bytes.size < 10) return

        val idxByte1 = bytes[3]
        val ldvTimerIndex1 = TimerIndex.fromByte(idxByte1) ?: return
        val timerType1 = ldvTimerIndex1.toTimerType()

        val hour1 = bytes[4].toUnsignedInt()
        val minute1 = bytes[5].toUnsignedInt()
        val delay1 = bytes[6].toUnsignedInt()
        val ldvWeekByte1 = bytes[8]
        val isEnabled1 = bytes[9] == 0x01.toByte()
        val repeatInfo1 = ldvWeekByte1.toUnsignedInt()

        val receivedTimer1 = DeviceTimer(
            deviceId = deviceId,
            timerType = timerType1,
            enabled = isEnabled1,
            hour = hour1,
            minute = minute1,
            delay = delay1,
            weekCycle = repeatInfo1
        )

        val idxByte2 = bytes[10]
        val ldvTimerIndex2 = TimerIndex.fromByte(idxByte2) ?: return
        val timerType2 = ldvTimerIndex2.toTimerType()

        val hour2 = bytes[11].toUnsignedInt()
        val minute2 = bytes[12].toUnsignedInt()
        val delay2 = bytes[13].toUnsignedInt()
        val ldvWeekByte2 = bytes[15]
        val isEnabled2 = bytes[16] == 0x01.toByte()
        val repeatInfo2 = ldvWeekByte2.toUnsignedInt()

        val receivedTimer2 = DeviceTimer(
            deviceId = deviceId,
            timerType = timerType2,
            enabled = isEnabled2,
            hour = hour2,
            minute = minute2,
            delay = delay2,
            weekCycle = repeatInfo2
        )

        Timber.tag(TAG).i("parseLdvBedsideTimingInfo: $deviceId -> $receivedTimer1 | $receivedTimer2")
        registry.updateTimerInfo(deviceId, receivedTimer1, receivedTimer2)
    }
}
