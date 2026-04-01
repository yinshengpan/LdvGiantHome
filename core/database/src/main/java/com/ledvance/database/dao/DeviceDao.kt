package com.ledvance.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.ledvance.database.model.DeviceBaseUpdateEntity
import com.ledvance.database.model.DeviceEntity
import com.ledvance.database.model.DeviceNameUpdateEntity
import com.ledvance.database.model.DevicePowerUpdateEntity
import com.ledvance.database.model.DeviceWithRuntimeConfig
import com.ledvance.domain.FirmwareVersion
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.command.giant.ModeId
import com.ledvance.domain.bean.command.giant.ModeType
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

    @Transaction
    @Query("select * from devices where device_id = :deviceId")
    suspend fun getDevice(deviceId: DeviceId): DeviceWithRuntimeConfig?

    @Transaction
    @Query("select * from devices")
    fun getDeviceListFlow(): Flow<List<DeviceWithRuntimeConfig>>

    @Query("select device_id from devices")
    fun getDeviceIdListFlow(): Flow<List<DeviceId>>

    @Transaction
    @Query("select * from devices where device_id = :deviceId")
    fun getDeviceFlow(deviceId: DeviceId): Flow<DeviceWithRuntimeConfig?>

    @Query("UPDATE devices SET power = :power WHERE device_id = :deviceId")
    suspend fun updateDevicePower(deviceId: DeviceId, power: Boolean)

    @Query("UPDATE devices SET h = :h, s = :s WHERE device_id = :deviceId")
    suspend fun updateDeviceHs(deviceId: DeviceId, h: Int, s: Int)

    @Query("UPDATE devices SET v = :v WHERE device_id = :deviceId")
    suspend fun updateDeviceV(deviceId: DeviceId, v: Int)

    @Query("UPDATE devices SET cct = :cct WHERE device_id = :deviceId")
    suspend fun updateDeviceCct(deviceId: DeviceId, cct: Int)

    @Query("UPDATE devices SET brightness = :brightness WHERE device_id = :deviceId")
    suspend fun updateDeviceBrightness(deviceId: DeviceId, brightness: Int)

    @Query("UPDATE devices SET speed = :speed WHERE device_id = :deviceId")
    suspend fun updateDeviceSpeed(deviceId: DeviceId, speed: Int)

    @Query("UPDATE devices SET mode_type = :modeType, mode_id = :modeId WHERE device_id = :deviceId")
    suspend fun updateModeId(deviceId: DeviceId, modeType: ModeType?, modeId: ModeId?)

    @Query("UPDATE devices SET firmware_version = :firmwareVersion, name=:deviceName WHERE device_id = :deviceId")
    suspend fun updateDeviceFirmwareVersion(deviceId: DeviceId, deviceName: String, firmwareVersion: FirmwareVersion)

    @Update(entity = DeviceEntity::class)
    suspend fun updateDevicePowerList(list: List<DevicePowerUpdateEntity>)

    @Update(entity = DeviceEntity::class)
    suspend fun updateDeviceNameList(list: List<DeviceNameUpdateEntity>)

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