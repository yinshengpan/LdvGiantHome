package com.ledvance.light.screen.music.engine

import com.ledvance.light.screen.music.fft.AudioFeatures

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:37
 * Describe : PartyStrategy
 */
class PartyStrategy : LightStrategy {

    private var hue = 0f

    override fun map(f: AudioFeatures): LightState {
        if (f.beat) {
            hue = (0..360).random().toFloat() // 节拍换色
        }

        val value = if (f.beat) 1f else f.amplitude
        val (r, g, b) = ColorMapper.hsvToRgb(hue, 1f, value)

        return LightState(
            (r * 255).toInt(),
            (g * 255).toInt(),
            (b * 255).toInt(),
            (value * 100).toInt()
        )
    }
}