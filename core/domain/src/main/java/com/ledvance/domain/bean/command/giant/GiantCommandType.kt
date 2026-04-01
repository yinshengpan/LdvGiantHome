package com.ledvance.domain.bean.command.giant

import com.ledvance.domain.bean.command.Command

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 11:45
 * Describe : CommandType
 */
enum class GiantCommandType(override val command: Byte) : Command {
    QueryDeviceInfo(0x00),     // 查询设备信息
    SetBrightness(0x01),       // 设置亮度
    SetSpeed(0x02),            // 设置速度
    SetModeOrScene(0x03),      // 设置模式或场景
    SetSwitch(0x04),           // 设置开关
    SetColour(0x05),           // 设置颜色
    SetMicRhythm(0x06),        // 设置麦克风律动效果
    SetMicSensitivity(0x07),   // 设置麦克风灵敏度
    SetLedCount(0x08),         // 设置灯珠点数
    SetWireOrder(0x09),        // 设置线束
    SetTimer(0x0A),            // 设置定时
    SetCurrentTime(0x0B),      // 设置设备当前时间 (Byte2=0x01)
    QueryCurrentTime(0x0B),    // 查询设备当前时间 (Byte2=0x02)
    DeviceReset(0x0C),         // 设备复位
    QueryDeviceState(0x15),    // 查询设备状态
    GetTimingInfo(0x16),       // 查询定时信息
    ;
}