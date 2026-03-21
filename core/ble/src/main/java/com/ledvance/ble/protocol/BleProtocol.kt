package com.ledvance.ble.protocol

import com.ledvance.domain.bean.TimerType
import com.ledvance.domain.bean.command.BrightnessType
import com.ledvance.domain.bean.command.ColourType
import com.ledvance.domain.bean.command.LineSequence
import com.ledvance.domain.bean.command.ModeId
import com.ledvance.domain.bean.command.OnOffType
import com.ledvance.domain.bean.command.scenes.Scene

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:23
 * Describe : BleProtocol
 */
interface BleProtocol {
    suspend fun queryDeviceInfo(): Boolean
    suspend fun setBrightness(target: BrightnessType, brightness: Int): Boolean
    suspend fun setSpeed(speed: Int): Boolean
    suspend fun setMode(modeId: ModeId): Boolean
    suspend fun setPower(power: Boolean, onOffType: OnOffType = OnOffType.ALL): Boolean
    suspend fun setHSV(h: Int, s: Int): Boolean
    suspend fun setCCT(cct: Int): Boolean
    suspend fun setScene(sceneId: Scene): Boolean
    suspend fun setColor(type: ColourType, param1: Int, param2: Int, param3: Int): Boolean
    suspend fun setMicRhythmEffect(effect: Int): Boolean
    suspend fun setMicSensitivity(sensitivity: Int): Boolean
    suspend fun setLedCount(count: Int): Boolean
    suspend fun setLineSequence(lineSequence: LineSequence): Boolean
    suspend fun setTimer(timerType: TimerType, hour: Int, min: Int, weekCycle: Int): Boolean
    suspend fun queryTimer(): Boolean
    suspend fun setCurrentTime(hour: Int, min: Int, sec: Int, weekDay: Int): Boolean
    suspend fun syncCurrentTime(): Boolean
    suspend fun queryCurrentTime(): Boolean
    suspend fun resetDevice(): Boolean
}