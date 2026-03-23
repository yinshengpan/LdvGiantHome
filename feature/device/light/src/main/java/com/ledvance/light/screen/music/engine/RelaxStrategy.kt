package com.ledvance.light.screen.music.engine

import com.ledvance.light.screen.music.fft.AudioFeatures

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:38
 * Describe : RelaxStrategy
 */
class RelaxStrategy : LightStrategy {

    private var hue = 0f

    override fun map(f: AudioFeatures): LightState {
        hue += 0.2f
        if (hue > 360) hue -= 360

        val saturation = 0.6f + f.amplitude * 0.3f
        val value = 0.25f + f.amplitude * 0.75f

        val (r, g, b) = ColorMapper.hsvToRgb(hue, saturation, value)

        return LightState(
            (r * 255).toInt(),
            (g * 255).toInt(),
            (b * 255).toInt(),
            (value * 100).toInt()
        )
    }
}