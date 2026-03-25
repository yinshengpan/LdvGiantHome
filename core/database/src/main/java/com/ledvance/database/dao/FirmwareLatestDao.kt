package com.ledvance.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ledvance.database.model.FirmwareLatestEntity
import com.ledvance.domain.bean.DeviceType
import kotlinx.coroutines.flow.Flow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26
 * Describe : FirmwareLatestDao
 */
@Dao
interface FirmwareLatestDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFirmwareLatest(firmware: FirmwareLatestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertFirmwareLatestList(firmwares: List<FirmwareLatestEntity>)

    @Query("SELECT * FROM firmware_latest WHERE device_type = :deviceType")
    fun getFirmwareLatestFlow(deviceType: DeviceType): Flow<FirmwareLatestEntity?>
}
