package com.ledvance.database.converter

import androidx.room.TypeConverter
import com.ledvance.domain.bean.command.common.TimerType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26
 * Describe : TimerTypeConverter — Room TypeConverter for TimerType enum
 */
class TimerTypeConverter {
    @TypeConverter
    fun fromTimerType(value: TimerType): String = value.name

    @TypeConverter
    fun toTimerType(value: String): TimerType = TimerType.valueOf(value)
}
