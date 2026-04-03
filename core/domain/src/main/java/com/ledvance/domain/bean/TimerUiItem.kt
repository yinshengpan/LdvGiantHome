package com.ledvance.domain.bean

import com.ledvance.domain.bean.command.common.TimerType
import java.time.DayOfWeek

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26
 * Describe : TimerUiItem
 */
data class TimerUiItem(
    val timerType: TimerType,
    val enabled: Boolean = false,
    val hour: Int = 0,
    val minute: Int = 0,
    val displayTime: String = "00:00",
    val displayRepeat: String = "",
    val delay: Int = 0,
    val days: Set<DayOfWeek> = emptySet()
)

fun TimerUiItem.isGiantTimer(): Boolean {
    return this.timerType == TimerType.GiantOn || this.timerType == TimerType.GiantOff
}
