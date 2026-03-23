package com.ledvance.light.screen.music.fft

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:20
 * Describe : AudioFeatures
 */
data class AudioFeatures(
    val amplitude: Float,     // 0~1
    val low: Float,           // 低频能量
    val mid: Float,           // 中频
    val high: Float,          // 高频
    val beat: Boolean         // 是否节拍
)