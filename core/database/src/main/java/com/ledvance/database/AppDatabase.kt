package com.ledvance.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ledvance.database.converter.DeviceIdConverter
import com.ledvance.database.converter.DeviceTypeConverter
import com.ledvance.database.converter.FirmwareVersionConverter
import com.ledvance.database.converter.LineSequenceConverter
import com.ledvance.database.converter.ModeIdConverter
import com.ledvance.database.converter.ModeTypeConverter
import com.ledvance.database.converter.StringListConverter
import com.ledvance.database.converter.TimerTypeConverter
import com.ledvance.database.converter.WorkModeConverter
import com.ledvance.database.dao.DeviceDao
import com.ledvance.database.dao.DeviceRuntimeConfigDao
import com.ledvance.database.dao.FirmwareLatestDao
import com.ledvance.database.dao.TimerDao
import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.model.DeviceRuntimeConfigEntity
import com.ledvance.database.model.FirmwareLatestEntity
import com.ledvance.database.model.TimerEntity

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/23 17:40
 * Describe : AppDatabase
 */
@Database(
    entities = [
        DeviceEntity::class,
        TimerEntity::class,
        DeviceRuntimeConfigEntity::class,
        FirmwareLatestEntity::class,
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(
    value = [
        StringListConverter::class,
        DeviceTypeConverter::class,
        WorkModeConverter::class,
        DeviceIdConverter::class,
        TimerTypeConverter::class,
        ModeIdConverter::class,
        ModeTypeConverter::class,
        LineSequenceConverter::class,
        FirmwareVersionConverter::class,
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun timerDao(): TimerDao
    abstract fun deviceRuntimeConfigDao(): DeviceRuntimeConfigDao
    abstract fun firmwareLatestDao(): FirmwareLatestDao
}