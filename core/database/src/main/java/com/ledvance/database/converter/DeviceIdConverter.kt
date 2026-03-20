package com.ledvance.database.converter

import androidx.room.TypeConverter
import com.ledvance.domain.bean.DeviceId

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:11
 * Describe : DeviceIdConverter
 */
class DeviceIdConverter {
    @TypeConverter
    fun toDeviceId(value: String): DeviceId {
        return DeviceId(value)
    }

    @TypeConverter
    fun fromDeviceId(deviceId: DeviceId): String {
        return deviceId.macAddress
    }
}