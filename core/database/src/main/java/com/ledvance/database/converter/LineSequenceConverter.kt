package com.ledvance.database.converter

import androidx.room.TypeConverter
import com.ledvance.domain.bean.command.giant.LineSequence
import com.ledvance.utils.extensions.toUnsignedInt

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:11
 * Describe : LineSequenceConverter
 */
class LineSequenceConverter {
    @TypeConverter
    fun toLineSequence(value: Int): LineSequence? {
        return LineSequence.fromInt(value)
    }

    @TypeConverter
    fun fromLineSequence(lineSequence: LineSequence?): Int {
        return lineSequence?.command?.toUnsignedInt() ?: -1
    }
}