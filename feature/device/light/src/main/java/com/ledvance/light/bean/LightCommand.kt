package com.ledvance.light.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/22 13:58
 * Describe : LightCommand
 */
internal sealed interface LightCommand {
    data class ColourModeHs(val hue: Int, val sat: Int) : LightCommand
    data class ColourModeBrightness(val brightness: Int) : LightCommand
    data class WhiteModeCct(val cct: Int) : LightCommand
    data class WhiteModeBrightness(val brightness: Int) : LightCommand
    data class Speed(val speed: Int) : LightCommand
    data class DeviceMicSensitivity(val sensitivity: Int) : LightCommand
}