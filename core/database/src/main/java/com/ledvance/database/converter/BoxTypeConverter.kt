package com.ledvance.database.converter

import androidx.room.TypeConverter
import com.ledvance.database.model.BoxType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 09:20
 * Describe : BoxTypeConverter
 */
class BoxTypeConverter {
    @TypeConverter
    fun toBoxType(value: Int): BoxType {
        return BoxType.fromType(value)
    }

    @TypeConverter
    fun fromBoxType(mode: BoxType): Int {
        return mode.type
    }
}