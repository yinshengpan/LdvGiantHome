package com.ledvance.database.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.ledvance.domain.bean.DeviceId

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 14:42
 * Describe : DevicePowerUpdateEntity — 仅更新开关状态
 */
data class DevicePowerUpdateEntity(
    @PrimaryKey
    @ColumnInfo(name = "device_id")
    val deviceId: DeviceId,
    @ColumnInfo(name = "power")
    val power: Boolean,
)