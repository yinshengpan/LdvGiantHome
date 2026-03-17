package com.ledvance.database.converter

import androidx.room.TypeConverter
import com.ledvance.database.model.SetTripCurrentType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 09:20
 * Describe : SetTripCurrentTypeConverter
 */
class SetTripCurrentTypeConverter {
    @TypeConverter
    fun toSetTripCurrentType(value: Int): SetTripCurrentType {
        return SetTripCurrentType.fromType(value)
    }

    @TypeConverter
    fun fromSetTripCurrentType(mode: SetTripCurrentType): Int {
        return mode.type
    }
}