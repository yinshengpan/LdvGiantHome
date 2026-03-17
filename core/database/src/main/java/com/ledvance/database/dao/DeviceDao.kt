package com.ledvance.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ledvance.database.model.DeviceEntity
import kotlinx.coroutines.flow.Flow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/23 17:57
 * Describe : DeviceDao
 */
@Dao
interface DeviceDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(device: DeviceEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(devices: List<DeviceEntity>)

    @Query("select firmware_version from devices where address = :address")
    suspend fun getFMVersion(address: String): String?

    @Query("select sn from devices where address = :address")
    suspend fun getSN(address: String): String?

    @Query("select * from devices where address = :address")
    suspend fun getDevice(address: String): DeviceEntity?

    @Query("select * from devices")
    fun getDeviceListFlow(): Flow<List<DeviceEntity>>

    @Query("select * from devices where address = :address")
    fun getDeviceFlow(address: String): Flow<DeviceEntity?>

    @Delete
    suspend fun delete(device: DeviceEntity)

    @Update
    suspend fun update(device: DeviceEntity)

    @Query("delete from devices where address=:address")
    suspend fun deleteDevice(address: String)

    @Query("update devices set trip_current_value=:tripCurrent where address=:address")
    suspend fun updateTripCurrent(address: String,tripCurrent: Int)

}