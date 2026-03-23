package com.ledvance.utils

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 15:20
 * Describe : ColorUtils
 */
object ColorUtils {

    fun hsvToRgb(h: Int, s: Int, v: Int): IntArray {
        val hf = ((h % 360) + 360) % 360f
        val sf = s.toFloat() / 100f
        val vf = v.toFloat() / 100f

        val c = vf * sf
        val x = c * (1 - Math.abs((hf / 60) % 2 - 1))
        val m = vf - c

        val (r, g, b) = when {
            hf < 60 -> floatArrayOf(c, x, 0f)
            hf < 120 -> floatArrayOf(x, c, 0f)
            hf < 180 -> floatArrayOf(0f, c, x)
            hf < 240 -> floatArrayOf(0f, x, c)
            hf < 300 -> floatArrayOf(x, 0f, c)
            else -> floatArrayOf(c, 0f, x)
        }

        return intArrayOf(
            clamp(r + m),
            clamp(g + m),
            clamp(b + m),
        )
    }

    private fun clamp(x: Float): Int {
        return (x * 255).toInt().coerceIn(0, 255)
    }

    fun rgbToHsv(r: Int, g: Int, b: Int): IntArray {
        val rf = r / 255f
        val gf = g / 255f
        val bf = b / 255f

        val max = maxOf(rf, gf, bf)
        val min = minOf(rf, gf, bf)
        val delta = max - min

        // Hue
        val h = when {
            delta == 0f -> 0f
            max == rf -> 60 * (((gf - bf) / delta) % 6)
            max == gf -> 60 * (((bf - rf) / delta) + 2)
            else -> 60 * (((rf - gf) / delta) + 4)
        }.let {
            if (it < 0) it + 360 else it
        }

        // Saturation
        val s = if (max == 0f) 0f else delta / max

        // Value
        val v = max

        return intArrayOf(
            h.toInt(),
            (s * 100).toInt(),
            (v * 100).toInt()
        )
    }

    fun cctToWwCw(cct: Int): Pair<Int, Int> {
        val c = cct.coerceIn(0, 100)
        val warm = 100 - c
        val cool = c
        return warm to cool
    }
}