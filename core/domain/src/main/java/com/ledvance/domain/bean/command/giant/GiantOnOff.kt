package com.ledvance.domain.bean.command.giant

import com.ledvance.domain.bean.command.Command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 13:02
 * Describe : GiantOnOff
 */
enum class GiantOnOff(override val command: Byte) : Command {
    On(0x0F),
    Off(0x00)
}