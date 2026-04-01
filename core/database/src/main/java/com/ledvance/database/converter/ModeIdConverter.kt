package com.ledvance.database.converter

import androidx.room.TypeConverter
import com.ledvance.domain.bean.command.giant.ModeId
import com.ledvance.utils.extensions.toUnsignedInt

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:11
 * Describe : ModeIdConverter
 */
class ModeIdConverter {
    @TypeConverter
    fun toModeId(value: Int): ModeId? {
        return ModeId.fromInt(value)
    }

    @TypeConverter
    fun fromModeId(mode: ModeId?): Int {
        return mode?.command?.toUnsignedInt() ?: -1
    }
}