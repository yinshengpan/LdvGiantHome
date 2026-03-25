package com.ledvance.light.screen.music.effect

import com.ledvance.light.screen.music.analyzer.AudioFeature

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:32
 * Describe : ClassicLightEffect
 */
class ClassicLightEffect : LightEffectProvider {

    private var hue = 0f
    private var smoothedAmp = 0f
    private var maxAmp = 0.001f

    private var rSmooth = 0f
    private var gSmooth = 0f
    private var bSmooth = 0f

    override fun calculateLightState(f: AudioFeature): LightEffectState {
        // Track the envelope to get a dynamically scaled 0~1 amplitude
        smoothedAmp = smoothedAmp * 0.8f + f.amplitude * 0.2f
        maxAmp = maxOf(maxAmp, smoothedAmp)
        maxAmp -= 0.0005f
        if (maxAmp < 0.001f) maxAmp = 0.001f

        val normAmp = (smoothedAmp / maxAmp).coerceIn(0f, 1f)

        // Smooth frequencies to avoid harsh erratic jumps
        rSmooth = rSmooth * 0.8f + f.low * 0.2f
        gSmooth = gSmooth * 0.8f + f.mid * 0.2f
        bSmooth = bSmooth * 0.8f + f.high * 0.2f

        // Map highest frequency energy directly to Hue targets
        var targetHue = hue
        val totalFreq = rSmooth + gSmooth + bSmooth
        
        if (totalFreq > 0.001f) {
            targetHue = when {
                rSmooth > gSmooth && rSmooth > bSmooth -> 0f   // Red for Low/Bass
                gSmooth > rSmooth && gSmooth > bSmooth -> 120f // Green for Mid
                else -> 240f                                   // Blue for High
            }
        }

        // Smoothly transition current hue to target hue
        val diff = targetHue - hue
        val adjustedDiff = when {
            diff > 180f -> diff - 360f
            diff < -180f -> diff + 360f
            else -> diff
        }
        
        // 0.15f multiplier ensures color smoothly glides rather than flashing wildly
        hue += adjustedDiff * 0.15f
        if (hue < 0f) hue += 360f
        if (hue >= 360f) hue -= 360f

        // Maintain very vivid saturation and good brightness scaled via amplitude
        val saturation = 0.8f + (normAmp * 0.2f)
        val value = 0.4f + (normAmp * 0.6f)

        val (r, g, b) = ColorConverter.convertHsvToRgb(hue, saturation, value)

        return LightEffectState(
            (r * 255).toInt(),
            (g * 255).toInt(),
            (b * 255).toInt(),
            (value * 100).toInt()
        )
    }
}