package com.ledvance.light.screen.music.engine

import com.ledvance.light.screen.music.fft.AudioFeatures

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:32
 * Describe : ClassicStrategy
 */
class ClassicStrategy : LightStrategy {

    private var r = 0f
    private var g = 0f
    private var b = 0f
    private var brightness = 0f

    override fun map(f: AudioFeatures): LightState {
        r = smooth(r, f.low)
        g = smooth(g, f.mid)
        b = smooth(b, f.high)
        brightness = smooth(brightness, f.amplitude)

        return LightState(
            (r * 255).toInt(),
            (g * 255).toInt(),
            (b * 255).toInt(),
            (brightness * 100).toInt()
        )
    }

    private fun smooth(old: Float, new: Float) =
        old * 0.8f + new * 0.2f
}