package com.ledvance.ble.protocol

import com.ledvance.domain.bean.command.common.TimerRepeat
import com.ledvance.domain.bean.command.common.TimerType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:23
 * Describe : BleProtocol
 */
interface BleProtocol {
    suspend fun queryDeviceInfo(): Boolean
    suspend fun setBrightness(target: Int, brightness: Int): Boolean
    suspend fun setSpeed(speed: Int): Boolean
    suspend fun setModeId(modeId: Int): Boolean
    suspend fun setModeType(modeType: Int): Boolean
    suspend fun setPower(power: Boolean, onOffType: Int = 0): Boolean
    suspend fun setHs(h: Int, s: Int): Boolean
    suspend fun setRgb(r: Int, g: Int, b: Int): Boolean
    suspend fun setCct(cct: Int): Boolean
    suspend fun setScene(sceneId: Int): Boolean
    suspend fun setColor(type: Int, param1: Int, param2: Int, param3: Int): Boolean
    suspend fun setMicRhythmEffect(effect: Int): Boolean
    suspend fun setMicSensitivity(sensitivity: Int): Boolean
    suspend fun setLedCount(count: Int): Boolean
    suspend fun setLineSequence(lineSequence: Int): Boolean
    suspend fun setTimer(timerType: TimerType, hour: Int, min: Int, timerRepeat: TimerRepeat, delay: Int = 0): Boolean
    suspend fun queryTimer(): Boolean
    suspend fun setCurrentTime(hour: Int, min: Int, sec: Int, weekDay: Int): Boolean
    suspend fun syncCurrentTime(): Boolean
    suspend fun queryCurrentTime(): Boolean
    suspend fun resetDevice(): Boolean
}