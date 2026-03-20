package com.ledvance.domain.bean.command.timer

import com.ledvance.domain.bean.command.Command
import java.time.DayOfWeek

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 10:40
 * Describe : TimerDayOfWeek
 */
enum class TimerDayOfWeek(val dayOfWeek: DayOfWeek, override val command: Byte) : Command {
    MONDAY(DayOfWeek.MONDAY, 0x02),
    TUESDAY(DayOfWeek.TUESDAY, 0x03),
    WEDNESDAY(DayOfWeek.WEDNESDAY, 0x04),
    THURSDAY(DayOfWeek.THURSDAY, 0x05),
    FRIDAY(DayOfWeek.FRIDAY, 0x06),
    SATURDAY(DayOfWeek.SATURDAY, 0x07),
    SUNDAY(DayOfWeek.SUNDAY, 0x01);

    companion object {
        fun formDayOfWeek(dayOfWeek: DayOfWeek): TimerDayOfWeek {
            return entries.find { it.dayOfWeek == dayOfWeek } ?: MONDAY
        }
    }
}