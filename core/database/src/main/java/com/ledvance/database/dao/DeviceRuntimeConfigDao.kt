package com.ledvance.database.dao

import androidx.room.*
import com.ledvance.database.model.DeviceRuntimeConfigEntity
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.WorkMode
import com.ledvance.domain.bean.command.LineSequence

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/03/23 17:18
 * Describe : DeviceRuntimeConfigDao
 */
@Dao
interface DeviceRuntimeConfigDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertConfig(config: DeviceRuntimeConfigEntity)

    @Query("UPDATE device_runtime_config SET work_mode = :workMode WHERE device_id = :deviceId")
    suspend fun updateDeviceWorkMode(deviceId: DeviceId, workMode: WorkMode)

    @Query("UPDATE device_runtime_config SET line_sequence = :lineSequence WHERE device_id = :deviceId")
    suspend fun updateDeviceLineSequence(deviceId: DeviceId, lineSequence: LineSequence)

    @Query("UPDATE device_runtime_config SET phone_mic_sensitivity = :phoneMicSensitivity WHERE device_id = :deviceId")
    suspend fun updatePhoneMicSensitivity(deviceId: DeviceId, phoneMicSensitivity: Int)

    @Query("SELECT * FROM device_runtime_config WHERE device_id = :deviceId")
    suspend fun getDeviceConfig(deviceId: DeviceId): DeviceRuntimeConfigEntity?
}
