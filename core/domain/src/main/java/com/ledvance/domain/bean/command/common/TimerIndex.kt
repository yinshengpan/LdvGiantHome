package com.ledvance.domain.bean.command.common

import com.ledvance.domain.bean.command.Command

/**
 * @author : generated
 * Describe : TimerIndex — 定时器编号槽位
 * 闹钟index（唤醒0x01/0x02 ,睡眠0x03/0x04 )
 */
enum class TimerIndex(override val command: Byte) : Command {
    Wakeup1(0x01),
    Wakeup2(0x02),
    Sleep1(0x03),
    Sleep2(0x04),
    ;

    companion object {
        fun fromByte(value: Byte): TimerIndex? {
            return entries.find { it.command == value }
        }
    }
}

fun TimerIndex.toTimerType(): TimerType {
    return when (this) {
        TimerIndex.Wakeup1 -> TimerType.LdvWakeup1
        TimerIndex.Wakeup2 -> TimerType.LdvWakeup2
        TimerIndex.Sleep1 -> TimerType.LdvSleep1
        TimerIndex.Sleep2 -> TimerType.LdvSleep2
    }
}
