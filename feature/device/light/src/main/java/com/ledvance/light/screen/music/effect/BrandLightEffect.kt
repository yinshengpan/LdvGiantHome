package com.ledvance.light.screen.music.effect

import com.ledvance.light.screen.music.analyzer.AudioFeature

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:40
 * Describe : BrandLightEffect
 */
internal class BrandLightEffect : LightEffectProvider {

    private val baseHue = 30f // 橙色
    private var smoothedAmp = 0f
    private var maxAmp = 0.001f
    private var smoothedHigh = 0f
    private var maxHigh = 0.001f

    override fun calculateLightState(f: AudioFeature): LightEffectState {
        // Track overall amplitude envelope
        smoothedAmp = smoothedAmp * 0.8f + f.amplitude * 0.2f
        maxAmp = maxOf(maxAmp, smoothedAmp)
        maxAmp -= 0.0005f
        if (maxAmp < 0.001f) maxAmp = 0.001f
        val normAmp = (smoothedAmp / maxAmp).coerceIn(0f, 1f)

        // Track high frequency envelope for hue shifting
        smoothedHigh = smoothedHigh * 0.8f + f.high * 0.2f
        maxHigh = maxOf(maxHigh, smoothedHigh)
        maxHigh -= 0.0005f
        if (maxHigh < 0.001f) maxHigh = 0.001f
        val normHigh = (smoothedHigh / maxHigh).coerceIn(0f, 1f)

        // 色相偏移，高频相对越强，偏移越明显
        val hue = baseHue + (normHigh * 30f)
        
        // 饱和度和亮度都通过归一化的振幅来驱动
        val saturation = 0.8f + normAmp * 0.2f
        val value = 0.3f + normAmp * 0.7f

        val (r, g, b) = ColorConverter.convertHsvToRgb(hue, saturation, value)

        return LightEffectState(
            (r * 255).toInt(),
            (g * 255).toInt(),
            (b * 255).toInt(),
            (value * 100).toInt()
        )
    }
}