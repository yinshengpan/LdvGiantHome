package com.ledvance.domain.bean.command.timer

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 15:10
 * Describe : TimerRepeat
 */
import java.time.DayOfWeek

data class TimerRepeat(
    val enabled: Boolean,
    val days: Set<DayOfWeek>
)

fun Byte.toTimerRepeat(): TimerRepeat {
    val value = this.toInt() and 0xFF

    val enabled = (value shr 7) and 0x01 == 1

    val days = mutableSetOf<DayOfWeek>()

    for (i in 0..6) {
        if ((value shr i) and 0x01 == 1) {
            val day = when (i) {
                0 -> DayOfWeek.MONDAY
                1 -> DayOfWeek.TUESDAY
                2 -> DayOfWeek.WEDNESDAY
                3 -> DayOfWeek.THURSDAY
                4 -> DayOfWeek.FRIDAY
                5 -> DayOfWeek.SATURDAY
                6 -> DayOfWeek.SUNDAY
                else -> continue
            }
            days.add(day)
        }
    }

    return TimerRepeat(enabled, days)
}

fun TimerRepeat.toByte(): Byte {
    var value = 0

    if (enabled) {
        value = value or (1 shl 7)
    }

    days.forEach {
        val bit = when (it) {
            DayOfWeek.MONDAY -> 0
            DayOfWeek.TUESDAY -> 1
            DayOfWeek.WEDNESDAY -> 2
            DayOfWeek.THURSDAY -> 3
            DayOfWeek.FRIDAY -> 4
            DayOfWeek.SATURDAY -> 5
            DayOfWeek.SUNDAY -> 6
        }
        value = value or (1 shl bit)
    }

    return value.toByte()
}