package com.ledvance.database.converter

import androidx.room.TypeConverter
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:11
 * Describe : DeviceIdConverter
 */
class DeviceIdConverter {
    @TypeConverter
    fun toDeviceId(value: String): DeviceId {
        val split = value.split("_")
        val address = split.getOrNull(1) ?: ""
        val type = split.getOrNull(0)?.toIntOrNull() ?: 0
        return DeviceId(address, DeviceType.fromType(type))
    }

    @TypeConverter
    fun fromDeviceId(deviceId: DeviceId): String {
        return "${deviceId.deviceType}_${deviceId.macAddress}"
    }
}