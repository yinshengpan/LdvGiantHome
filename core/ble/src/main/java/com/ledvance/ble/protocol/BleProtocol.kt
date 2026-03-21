package com.ledvance.ble.protocol

import com.ledvance.domain.bean.TimerType
import com.ledvance.domain.bean.command.BrightnessType
import com.ledvance.domain.bean.command.ColourType
import com.ledvance.domain.bean.command.ModeId
import com.ledvance.domain.bean.command.ModeType
import com.ledvance.domain.bean.command.OnOffType
import com.ledvance.domain.bean.command.scenes.Scene

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:23
 * Describe : BleProtocol
 */
interface BleProtocol {
    suspend fun queryDeviceInfo()
    suspend fun setBrightness(target: BrightnessType, brightness: Int)
    suspend fun setSpeed(speed: Int)
    suspend fun setMode(modeId: ModeId)
    suspend fun setPower(power: Boolean, onOffType: OnOffType = OnOffType.ALL)
    suspend fun setHSV(h: Int, s: Int)
    suspend fun setCCT(cct: Int)
    suspend fun setScene(sceneId: Scene)
    suspend fun setColor(type: ColourType, param1: Int, param2: Int, param3: Int)
    suspend fun setMicRhythmEffect(effect: Int)
    suspend fun setMicSensitivity(sensitivity: Int)
    suspend fun setLedCount(count: Int)
    suspend fun setWireOrder(order: Int)
    suspend fun setTimer(timerType: TimerType, hour: Int, min: Int, weekCycle: Int)
    suspend fun queryTimer()
    suspend fun setCurrentTime(hour: Int, min: Int, sec: Int, weekDay: Int)
    suspend fun syncCurrentTime()
    suspend fun queryCurrentTime()
    suspend fun resetDevice()
}