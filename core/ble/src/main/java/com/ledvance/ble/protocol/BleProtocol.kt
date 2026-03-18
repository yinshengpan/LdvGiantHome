package com.ledvance.ble.protocol

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:23
 * Describe : BleProtocol
 */
interface BleProtocol {
    suspend fun on()
    suspend fun off()
    suspend fun setHSV(h: Int, s: Int, v: Int)
    suspend fun setCCT(temp: Int, brightness: Int)
}