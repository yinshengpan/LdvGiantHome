package com.ledvance.database.converter

import androidx.room.TypeConverter
import com.ledvance.domain.bean.command.common.ModeType
import com.ledvance.utils.extensions.toUnsignedInt

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:11
 * Describe : ModeTypeConverter
 */
class ModeTypeConverter {
    @TypeConverter
    fun toModeType(value: Int): ModeType? {
        return ModeType.fromInt(value)
    }

    @TypeConverter
    fun fromModeType(mode: ModeType?): Int {
        return mode?.command?.toUnsignedInt() ?: -1
    }
}