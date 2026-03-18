package com.ledvance.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ledvance.database.converter.DeviceTypeConverter
import com.ledvance.database.converter.StringListConverter
import com.ledvance.database.dao.DeviceDao
import com.ledvance.database.model.DeviceEntity

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/23 17:40
 * Describe : AppDatabase
 */
@Database(
    entities = [DeviceEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(StringListConverter::class, DeviceTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
}