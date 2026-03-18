package com.ledvance.database.converter

import androidx.room.TypeConverter
import com.ledvance.domain.bean.DeviceType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:11
 * Describe : DeviceTypeConverter
 */
class DeviceTypeConverter {
    @TypeConverter
    fun toDeviceType(value: Int): DeviceType {
        return DeviceType.fromType(value)
    }

    @TypeConverter
    fun fromDeviceType(mode: DeviceType): Int {
        return mode.type
    }
}