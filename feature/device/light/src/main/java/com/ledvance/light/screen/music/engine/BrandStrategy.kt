package com.ledvance.light.screen.music.engine

import com.ledvance.light.screen.music.fft.AudioFeatures

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:40
 * Describe : BrandStrategy
 */
class BrandStrategy : LightStrategy {

    private val baseHue = 30f // 橙色

    override fun map(f: AudioFeatures): LightState {
        val hue = baseHue + (f.high * 20f) // 轻微偏移
        val saturation = 0.8f
        val value = 0.3f + f.amplitude * 0.7f

        val (r, g, b) = ColorMapper.hsvToRgb(hue, saturation, value)

        return LightState(
            (r * 255).toInt(),
            (g * 255).toInt(),
            (b * 255).toInt(),
            (value * 100).toInt()
        )
    }
}