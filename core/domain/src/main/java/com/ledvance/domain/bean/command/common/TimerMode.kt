package com.ledvance.domain.bean.command.common

import com.ledvance.domain.bean.command.Command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/2/26 09:25
 * Describe : TimerMode
 */
enum class TimerMode(override val command: Byte) : Command {
    WakeUp(0x01),
    Sleep(0x02)
}