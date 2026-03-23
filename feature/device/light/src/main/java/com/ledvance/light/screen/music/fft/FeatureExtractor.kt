package com.ledvance.light.screen.music.fft

import kotlin.math.abs
import kotlin.math.ln

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:19
 * Describe : FeatureExtractor
 */
object FeatureExtractor {

    private var lastLow = 0f
    private var lastBeatTime = 0L

    fun from(fft: FloatArray, amplitude: Float): AudioFeatures {
        val size = fft.size

        val lowEnd = size / 8
        val midEnd = size / 2

        val low = avgAbs(fft, 0, lowEnd)
        val mid = avgAbs(fft, lowEnd, midEnd)
        val high = avgAbs(fft, midEnd, size)

        val beat = detectBeatFromLow(low)

        return AudioFeatures(
            amplitude = compress(amplitude),
            low = normalize(low),
            mid = normalize(mid),
            high = normalize(high),
            beat = beat
        )
    }

    private fun avgAbs(fft: FloatArray, start: Int, end: Int): Float {
        var sum = 0f
        for (i in start until end) {
            sum += abs(fft[i])
        }
        return sum / (end - start)
    }

    private fun detectBeatFromLow(low: Float): Boolean {
        val now = System.currentTimeMillis()

        val smoothLow = lastLow * 0.8f + low * 0.2f

        val isBeat = smoothLow > lastLow * 1.3f &&
                smoothLow > 0.01f &&
                now - lastBeatTime > 120

        if (isBeat) lastBeatTime = now
        lastLow = smoothLow

        return isBeat
    }

    private fun compress(x: Float): Float {
        return (ln(1 + x * 9) / ln(10f)).coerceIn(0f, 1f)
    }

    private fun normalize(x: Float): Float {
        return (x / 1000f).coerceIn(0f, 1f)
    }
}