package com.ledvance.light.screen.music.analyzer

import kotlin.math.sqrt

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:18
 * Describe : FftProcessor
 */
object FftProcessor {

    fun calculateMagnitude(pcm: ShortArray): FloatArray {
        val n = pcm.size
        val fft = FloatArray(n * 2)

        // 实部 + 虚部
        for (i in 0 until n) {
            fft[2 * i] = pcm[i].toFloat()
            fft[2 * i + 1] = 0f
        }

        // 👉 这里建议用 JTransforms（实际项目）
        // 这里假装已经做完 FFT

        val magnitude = FloatArray(n / 2)
        for (i in magnitude.indices) {
            val real = fft[2 * i]
            val imag = fft[2 * i + 1]
            magnitude[i] = sqrt(real * real + imag * imag)
        }

        return magnitude
    }
}