package com.ledvance.domain.bean

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26
 * Describe : DeviceTimer — 设备定时器领域模型
 */
data class DeviceTimer(
    val deviceId: DeviceId,
    val timerType: TimerType,
    val enabled: Boolean,
    val hour: Int,       // 0-23
    val minute: Int,     // 0-59
    val weekCycle: Int,
)
