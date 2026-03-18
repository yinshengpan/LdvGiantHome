package com.ledvance.ble.protocol

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:23
 * Describe : BleProtocol
 */
interface BleProtocol {
    suspend fun queryDeviceInfo()
    suspend fun setBrightness(target: Int, brightness: Int)
    suspend fun setSpeed(speed: Int)
    suspend fun setMode(type: Int, modeId: Int)
    suspend fun on(target: Int = 0)
    suspend fun off(target: Int = 0)
    suspend fun setHSV(h: Int, s: Int, v: Int)
    suspend fun setCCT(temp: Int, brightness: Int)
    suspend fun setScene(sceneId: Int)
    suspend fun setColor(type: Int, param1: Int, param2: Int, param3: Int)
    suspend fun setMicRhythmEffect(effect: Int)
    suspend fun setMicSensitivity(sensitivity: Int)
    suspend fun setLedCount(count: Int)
    suspend fun setWireOrder(order: Int)
    suspend fun setTimer(switchState: Boolean, hour: Int, min: Int, weekCycle: Int)
    suspend fun queryTimer()
    suspend fun setCurrentTime(hour: Int, min: Int, sec: Int, weekDay: Int)
    suspend fun queryCurrentTime()
    suspend fun resetDevice()
    suspend fun getTimingInfo()
}