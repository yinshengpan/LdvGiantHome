package com.ledvance.database.converter

import androidx.room.TypeConverter
import com.ledvance.domain.FirmwareVersion

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:11
 * Describe : FirmwareVersionConverter
 */
class FirmwareVersionConverter {
    @TypeConverter
    fun toFirmwareVersion(value: Int): FirmwareVersion {
        return FirmwareVersion.create(value)
    }

    @TypeConverter
    fun fromFirmwareVersion(version: FirmwareVersion): Int {
        return version.value
    }
}