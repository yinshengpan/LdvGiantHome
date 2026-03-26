package com.ledvance.music.effect

import com.ledvance.music.analyzer.AudioFeature

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:37
 * Describe : PartyLightEffect
 */
internal class PartyLightEffect : LightEffectProvider {

    private var hue = 0f

    override fun calculateLightState(f: AudioFeature): LightEffectState {
        if (f.beat) {
            hue = (0..360).random().toFloat() // 节拍换色
        }

        val value = if (f.beat) 1f else f.amplitude
        val (r, g, b) = ColorConverter.convertHsvToRgb(hue, 1f, value)

        return LightEffectState(
            (r * 255).toInt(),
            (g * 255).toInt(),
            (b * 255).toInt(),
            (value * 100).toInt()
        )
    }
}