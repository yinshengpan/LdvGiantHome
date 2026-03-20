package com.ledvance.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.ledvance.database.model.DeviceBaseUpdateEntity
import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.model.DevicePowerUpdateEntity
import com.ledvance.domain.bean.DeviceId
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

    @Query("select * from devices where device_id = :deviceId")
    suspend fun getDevice(deviceId: DeviceId): DeviceEntity?

    @Query("select * from devices")
    fun getDeviceListFlow(): Flow<List<DeviceEntity>>

    @Query("select device_id from devices")
    fun getDeviceIdListFlow(): Flow<List<DeviceId>>

    @Query("select * from devices where device_id = :deviceId")
    fun getDeviceFlow(deviceId: DeviceId): Flow<DeviceEntity?>

    @Query("UPDATE devices SET power = :power WHERE device_id = :deviceId")
    suspend fun updateDevicePower(deviceId: DeviceId, power: Boolean)

    @Update(entity = DeviceEntity::class)
    suspend fun updateDevicePowerList(list: List<DevicePowerUpdateEntity>)

    /** 持久化蓝牙回响的基础状态数据 */
    @Update(entity = DeviceEntity::class)
    suspend fun updateBaseInfo(baseInfo: DeviceBaseUpdateEntity)

    /** 持久化蓝牙回响的基础状态数据 */
    @Update(entity = DeviceEntity::class)
    suspend fun updateBaseInfoList(baseInfoList: List<DeviceBaseUpdateEntity>)

    @Delete
    suspend fun delete(device: DeviceEntity)

    @Update
    suspend fun update(device: DeviceEntity)

    @Query("delete from devices where device_id=:deviceId")
    suspend fun deleteDevice(deviceId: DeviceId)
}