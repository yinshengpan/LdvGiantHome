package com.ledvance.ui.component.rectpicker

import androidx.compose.ui.graphics.Color
import kotlin.math.ln
import kotlin.math.pow


internal object ColorUtils {

    fun hsvToColor(hsv: Hsv): Color {
        val baseColor = Color.hsv(hsv.hue.coerceIn(0f, 360f), hsv.saturation.coerceIn(0f, 1f), 1f)
        return baseColor
    }

    fun brightKelvinToColor(temperature: Int): Color {
        val kelvin = 4000f + (temperature / 100f) * 4000f
        val baseColor = kelvinToColor(kelvin.toDouble())
        return baseColor
    }

    /**
     * Maps a brightness value from a given range to an alpha value (0.2 to 1.0).
     */
    private fun brightToOpacity(
        brightness: Float,
        min: Float,
        max: Float,
        minOpacity: Float = 0.2f,
        maxOpacity: Float = 1.0f
    ): Float {
        if (min == max) return maxOpacity
        val ratio = ((brightness - min) / (max - min)).coerceIn(0f, 1f)
        return minOpacity + ratio * (maxOpacity - minOpacity)
    }

    /**
     * Converts a color temperature in Kelvin to a Jetpack Compose Color.
     * This is a standard algorithm for approximating RGB from Kelvin.
     */
    private fun kelvinToColor(tempKelvin: Double): Color {
        val temp = tempKelvin / 100.0

        val red = if (temp <= 66) {
            255.0
        } else {
            val r = temp - 60
            329.698727446 * r.pow(-0.1332047592)
        }.coerceIn(0.0, 255.0).toInt()

        val green = if (temp <= 66) {
            99.4708025861 * ln(temp) - 161.1195681661
        } else {
            val g = temp - 60
            288.1221695283 * g.pow(-0.0755148492)
        }.coerceIn(0.0, 255.0).toInt()

        val blue = when {
            temp >= 66 -> 255.0
            temp <= 19 -> 0.0
            else -> {
                val b = temp - 10
                138.5177312231 * ln(b) - 305.0447927307
            }
        }.coerceIn(0.0, 255.0).toInt()

        return Color(red, green, blue)
    }
}