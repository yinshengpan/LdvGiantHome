package com.ledvance.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.model.DeviceSwitchUpdateEntity
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

    @Query("select * from devices where address = :address")
    suspend fun getDevice(address: String): DeviceEntity?

    @Query("select * from devices")
    fun getDeviceListFlow(): Flow<List<DeviceEntity>>

    @Query("select address from devices")
    fun getDeviceIdListFlow(): Flow<List<String>>

    @Query("select * from devices where address = :address")
    fun getDeviceFlow(address: String): Flow<DeviceEntity?>

    @Query("UPDATE devices SET switch_state = :switch WHERE address = :address")
    suspend fun updateDeviceSwitch(address: String, switch: Boolean)

    @Update(entity = DeviceEntity::class)
    suspend fun updateDeviceSwitchList(list: List<DeviceSwitchUpdateEntity>)

    @Delete
    suspend fun delete(device: DeviceEntity)

    @Update
    suspend fun update(device: DeviceEntity)

    @Query("delete from devices where address=:address")
    suspend fun deleteDevice(address: String)
}