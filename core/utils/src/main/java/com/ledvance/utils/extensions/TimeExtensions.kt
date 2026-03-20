package com.ledvance.utils.extensions

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/24/25 10:46
 * Describe : TimeExtensions
 */

private val formatter by lazy {
    DateTimeFormatter
        .ofLocalizedDateTime(FormatStyle.MEDIUM)
        .withLocale(Locale.getDefault())
}

fun Long.toTimeStr(): String {
    return tryCatchReturn { formatter.format(toLocalDateTime()) } ?: ""
}

fun Long.toLocalDateTime(): LocalDateTime {
    return Instant.ofEpochMilli(this)
        .atZone(ZoneId.systemDefault())
        .toLocalDateTime()
}

data class TimeInfo(
    val year: Int,
    val month: Int,
    val day: Int,
    val hour: Int,
    val minute: Int,
    val second: Int,
    val week: DayOfWeek,
)

fun LocalDateTime.toTimeInfo(): TimeInfo {
    return TimeInfo(
        year = this.year,
        month = this.monthValue,
        day = this.dayOfMonth,
        hour = this.hour,
        minute = this.minute,
        second = this.second,
        week = this.dayOfWeek,
    )
}