package com.ledvance.domain.bean.command.common

import com.ledvance.domain.bean.command.Command
import com.ledvance.domain.bean.command.giant.GiantOnOff

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26
 * Describe : TimerType — 定时器类型（开灯 / 关灯），通用于 Giant 和 LDV 设备
 */
enum class TimerType(val mode: Byte, override val command: Byte) : Command {
    // Giant中 // 0x01 表示设置设备定时命令;  0x02 表示查询设备定时命令;  0x03 表示设备返回定时数据
    GiantOn(0x01, GiantOnOff.On.command),   // 开灯定时
    GiantOff(0x01, GiantOnOff.Off.command),   // 关灯定时
    LdvWakeup1(TimerMode.WakeUp.command, TimerIndex.Wakeup1.command),
    LdvWakeup2(TimerMode.WakeUp.command, TimerIndex.Wakeup2.command),
    LdvSleep1(TimerMode.Sleep.command, TimerIndex.Sleep1.command),
    LdvSleep2(TimerMode.Sleep.command, TimerIndex.Sleep2.command),
    ;
}
