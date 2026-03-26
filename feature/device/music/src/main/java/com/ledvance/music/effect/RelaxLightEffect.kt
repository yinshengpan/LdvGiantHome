package com.ledvance.music.effect

import com.ledvance.music.analyzer.AudioFeature

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:38
 * Describe : RelaxLightEffect
 */
internal class RelaxLightEffect : LightEffectProvider {

    private var hue = 0f
    private var smoothedAmp = 0f
    private var maxAmp = 0.001f

    override fun calculateLightState(f: AudioFeature): LightEffectState {
        // Track the envelope to get a dynamically scaled 0~1 amplitude
        smoothedAmp = smoothedAmp * 0.8f + f.amplitude * 0.2f
        maxAmp = maxOf(maxAmp, smoothedAmp)
        // Allow maxAmp to slowly decay over time
        maxAmp -= 0.0005f
        if (maxAmp < 0.001f) maxAmp = 0.001f

        val normAmp = (smoothedAmp / maxAmp).coerceIn(0f, 1f)

        // Hue shifts faster when amplitude is higher
        hue += 0.2f + (normAmp * 0.5f)
        if (hue > 360f) hue -= 360f

        // Let the relative amplitude noticeably drive saturation and value
        val saturation = 0.5f + normAmp * 0.5f
        val value = 0.4f + normAmp * 0.6f

        val (r, g, b) = ColorConverter.convertHsvToRgb(hue, saturation, value)

        return LightEffectState(
            (r * 255).toInt(),
            (g * 255).toInt(),
            (b * 255).toInt(),
            (value * 100).toInt()
        )
    }
}