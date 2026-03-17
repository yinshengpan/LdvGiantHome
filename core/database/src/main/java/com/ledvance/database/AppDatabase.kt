package com.ledvance.database

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ledvance.database.converter.BoxTypeConverter
import com.ledvance.database.converter.NetworkModeConverter
import com.ledvance.database.converter.StringListConverter
import com.ledvance.database.dao.ChargerDao
import com.ledvance.database.dao.DeviceDao
import com.ledvance.database.dao.SetTripCurrentHistoryDao
import com.ledvance.database.model.ChargerEntity
import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.model.SetTripCurrentHistoryEntity
import com.ledvance.database.spec.DeleteDeviceColumns3To4Spec
import com.ledvance.database.spec.DeleteDeviceColumns4To5Spec

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/23 17:40
 * Describe : AppDatabase
 */
@Database(
    entities = [DeviceEntity::class, ChargerEntity::class, SetTripCurrentHistoryEntity::class],
    version = 5,
    autoMigrations = [
        AutoMigration(1, 2),
        AutoMigration(2, 3),
        AutoMigration(3, 4, spec = DeleteDeviceColumns3To4Spec::class),
        AutoMigration(4, 5, spec = DeleteDeviceColumns4To5Spec::class),
    ],
    exportSchema = true
)
@TypeConverters(NetworkModeConverter::class, BoxTypeConverter::class, StringListConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun deviceDao(): DeviceDao
    abstract fun chargerDao(): ChargerDao
    abstract fun setTripCurrentHistoryDao(): SetTripCurrentHistoryDao
}