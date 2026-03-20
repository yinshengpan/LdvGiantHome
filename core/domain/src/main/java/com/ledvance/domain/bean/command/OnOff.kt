package com.ledvance.domain.bean.command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 13:02
 * Describe : OnOff
 */
enum class OnOff(override val command: Byte) : Command {
    On(0x0F),
    Off(0x00)
}