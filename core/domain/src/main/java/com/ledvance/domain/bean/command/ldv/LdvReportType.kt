package com.ledvance.domain.bean.command.ldv

import com.ledvance.domain.bean.command.Command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/1/26
 * Describe : LdvReportType — LDV Bedside 设备上报命令类型 (上行帧 [0x5B][cmd])
 */
enum class LdvReportType(override val command: Byte) : Command {
    DeviceStatus(0x20),    // 灯状态上报: 开关+亮度+色温+模式
    TimerStatus(0x21),     // 定时任务上报
}
