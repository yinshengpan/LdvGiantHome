package com.ledvance.ble.core.parser

import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.ble.protocol.GiantProtocol
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceTimer
import com.ledvance.domain.bean.command.common.TimerType
import com.ledvance.domain.bean.command.giant.GiantCommandType
import com.ledvance.domain.bean.command.giant.GiantOnOff
import com.ledvance.domain.bean.command.NotifyType
import com.ledvance.utils.extensions.toUnsignedInt
import timber.log.Timber

class GiantNotificationParser(
    private val registry: DeviceRegistry
) : BleNotificationParser {

    override val TAG = "GiantNotification"

    override fun parse(deviceId: DeviceId, bytes: ByteArray) {
        if (bytes.size < 4) return
        if (bytes.first() != GiantProtocol.HEADER_BYTE) return
        if (bytes.last() != GiantProtocol.END_BYTE) return
        if (bytes[2] != NotifyType.GiantResponse.command) return

        val cmd = bytes[1]
        Timber.tag(TAG).d("parse: Identified command: 0x%02X for $deviceId", cmd)
        when (cmd) {
            GiantCommandType.QueryDeviceInfo.command -> parseQueryDeviceInfo(deviceId, bytes)
            GiantCommandType.QueryDeviceState.command -> parseQueryDeviceState(deviceId, bytes)
            GiantCommandType.GetTimingInfo.command -> parseGetTimingInfo(deviceId, bytes)
            GiantCommandType.QueryCurrentTime.command -> parseQueryCurrentTime(deviceId, bytes)
        }
    }

    private fun parseQueryDeviceInfo(deviceId: DeviceId, bytes: ByteArray) {
        if (bytes.size != 14) return
        val power = bytes[3] == GiantOnOff.On.command
        val r = bytes[4].toUnsignedInt()
        val g = bytes[5].toUnsignedInt()
        val b = bytes[6].toUnsignedInt()
        val w = bytes[7].toUnsignedInt().coerceIn(0, 100)
        val brightness = bytes[8].toUnsignedInt().coerceIn(1, 100)
        val modeType = bytes[9].toUnsignedInt()
        val modeId = bytes[10].toUnsignedInt()
        val speed = bytes[11].toUnsignedInt()
        Timber.tag(TAG).i("parseQueryDeviceInfo: $deviceId -> Power=$power, RGBW=($r,$g,$b,$w), Brightness=$brightness, ModeType=$modeType, ModeId=$modeId, Speed=$speed")
        registry.updateDeviceInfo(
            deviceId = deviceId,
            power = power,
            r = r,
            g = g,
            b = b,
            w = w,
            brightness = brightness,
            modeType = modeType,
            modeId = modeId,
            speed = speed
        )
    }

    private fun parseQueryDeviceState(deviceId: DeviceId, bytes: ByteArray) {
        if (bytes.size != 9) return
        val power = bytes[3] == GiantOnOff.On.command
        Timber.tag(TAG).i("parseQueryDeviceState: $deviceId -> Power=$power")
        registry.updateDeviceState(deviceId, power)
    }

    private fun parseGetTimingInfo(deviceId: DeviceId, bytes: ByteArray) {
        if (bytes.size != 13) return
        val onSwitch = bytes[3] == 0x01.toByte()
        val onHour = bytes[4].toUnsignedInt()
        val onMinute = bytes[5].toUnsignedInt()
        val onCycle = bytes[6].toUnsignedInt()
        val onTimer = DeviceTimer(
            deviceId = deviceId,
            timerType = TimerType.GiantOn,
            enabled = onSwitch,
            hour = onHour,
            minute = onMinute,
            delay = 0,
            weekCycle = onCycle,
        )
        val offSwitch = bytes[7] == 0x01.toByte()
        val offHour = bytes[8].toUnsignedInt()
        val offMinute = bytes[9].toUnsignedInt()
        val offCycle = bytes[10].toUnsignedInt()
        val offTimer = DeviceTimer(
            deviceId = deviceId,
            timerType = TimerType.GiantOff,
            enabled = offSwitch,
            hour = offHour,
            minute = offMinute,
            delay = 0,
            weekCycle = offCycle,
        )
        Timber.tag(TAG).i("parseGetTimingInfo: $deviceId -> ON(%s):%02d:%02d Cycle:%X, OFF(%s):%02d:%02d Cycle:%X",
            onSwitch, onHour, onMinute, onCycle, offSwitch, offHour, offMinute, offCycle)
        registry.updateTimerInfo(deviceId, onTimer, offTimer)
    }

    private fun parseQueryCurrentTime(deviceId: DeviceId, bytes: ByteArray) {
        if (bytes.size != 9) return
        Timber.tag(TAG).d("parseQueryCurrentTime: $deviceId -> Refreshed active time")
        registry.updateActive(deviceId)
    }
}
