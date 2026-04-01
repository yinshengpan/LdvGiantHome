package com.ledvance.domain.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/22 13:58
 * Describe : LightCommand
 */
sealed interface LightCommand {
    data class ColourModeHs(val hue: Int, val sat: Int, val brightness: Int? = null) : LightCommand
    data class ColourModeBrightness(val brightness: Int) : LightCommand
    data class WhiteModeCct(val cct: Int, val brightness: Int? = null) : LightCommand
    data class WhiteModeBrightness(val brightness: Int) : LightCommand
    data class Speed(val speed: Int) : LightCommand
    data class DeviceMicSensitivity(val sensitivity: Int) : LightCommand
}
