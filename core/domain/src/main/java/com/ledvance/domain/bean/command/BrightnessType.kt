package com.ledvance.domain.bean.command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 16:58
 * Describe : BrightnessType
 */
enum class BrightnessType(override val command: Byte) : Command {
    All(0x00),
    RGB(0x01),
    W(0x02),
    CT(0x03),
    WCT(0x04),
}