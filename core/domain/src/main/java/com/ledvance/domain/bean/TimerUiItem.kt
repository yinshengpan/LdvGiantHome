package com.ledvance.domain.bean

import java.time.DayOfWeek
import java.util.Locale

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
    val displayRepeat: String = "Never",
    val days: Set<DayOfWeek> = emptySet()
)

fun Set<DayOfWeek>.toDisplayText(locale: Locale = Locale.ENGLISH): String {
    if (isEmpty()) return "Never"
    if (size == 7) return "Every day"
    return this
        .sortedBy { it.value } // 周一~周日排序
        .joinToString(",") {
            it.name.take(3).lowercase()
                .replaceFirstChar { c -> c.uppercase() }
        }
}
