package com.ledvance.light.screen.music.effect

import kotlin.math.abs

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 10:39
 * Describe : ColorConverter
 */
object ColorConverter {

    fun convertHsvToRgb(h: Float, s: Float, v: Float): FloatArray {
        val hf = ((h % 360f) + 360f) % 360f
        val sf = s.coerceIn(0f, 1f)
        val vf = v.coerceIn(0f, 1f)

        val c = vf * sf
        val x = c * (1 - abs((hf / 60f) % 2 - 1))
        val m = vf - c

        val (r1, g1, b1) = when {
            hf < 60 -> floatArrayOf(c, x, 0f)
            hf < 120 -> floatArrayOf(x, c, 0f)
            hf < 180 -> floatArrayOf(0f, c, x)
            hf < 240 -> floatArrayOf(0f, x, c)
            hf < 300 -> floatArrayOf(x, 0f, c)
            else -> floatArrayOf(c, 0f, x)
        }

        return floatArrayOf(r1 + m, g1 + m, b1 + m)
    }
}