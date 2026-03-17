package com.ledvance.database.converter

import androidx.room.TypeConverter
import com.ledvance.database.model.NetworkMode

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 09:19
 * Describe : NetworkModeConverter
 */
class NetworkModeConverter {

    @TypeConverter
    fun toNetworkMode(value: Int): NetworkMode {
        return NetworkMode.fromType(value)
    }

    @TypeConverter
    fun fromNetworkMode(mode: NetworkMode): Int {
        return mode.type
    }
}