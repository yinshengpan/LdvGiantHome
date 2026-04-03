package com.ledvance.domain.bean

import com.ledvance.domain.bean.command.common.TimerRepeat
import com.ledvance.domain.bean.command.common.TimerType
import com.ledvance.domain.bean.command.common.toGiantTimerRepeat
import com.ledvance.domain.bean.command.common.toLdvTimerRepeat

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
    val delay: Int,
    val weekCycle: Int,
)

fun DeviceTimer.isLdvTimer(): Boolean {
    return when (timerType) {
        TimerType.LdvWakeup1, TimerType.LdvWakeup2, TimerType.LdvSleep1, TimerType.LdvSleep2 -> true
        else -> false
    }
}

fun DeviceTimer.isGiantTimer(): Boolean {
    return when (timerType) {
        TimerType.GiantOn, TimerType.GiantOff -> true
        else -> false
    }
}

fun DeviceTimer.getTimerRepeat(): TimerRepeat {
    return when {
        isGiantTimer() -> weekCycle.toByte().toGiantTimerRepeat()
        isLdvTimer() -> weekCycle.toByte().toLdvTimerRepeat(enabled)
        else -> weekCycle.toByte().toLdvTimerRepeat(enabled)
    }
}
