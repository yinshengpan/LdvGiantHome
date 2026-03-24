package com.ledvance.domain.bean

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
    val days: Set<DayOfWeek> = emptySet()
)
