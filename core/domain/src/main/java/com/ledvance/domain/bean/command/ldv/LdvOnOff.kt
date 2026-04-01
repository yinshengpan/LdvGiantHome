package com.ledvance.domain.bean.command.ldv

import com.ledvance.domain.bean.command.Command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 13:02
 * Describe : LdvOnOff
 */
enum class LdvOnOff(override val command: Byte) : Command {
    On(0x01),
    Off(0x00)
}