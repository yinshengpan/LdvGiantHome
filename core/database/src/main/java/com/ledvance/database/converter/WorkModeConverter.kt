package com.ledvance.database.converter

import androidx.room.TypeConverter
import com.ledvance.domain.bean.WorkMode

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:11
 * Describe : WorkModeConverter
 */
class WorkModeConverter {
    @TypeConverter
    fun toWorkMode(value: String): WorkMode {
        return WorkMode.of(value)
    }

    @TypeConverter
    fun fromWorkMode(mode: WorkMode): String {
        return mode.value
    }
}