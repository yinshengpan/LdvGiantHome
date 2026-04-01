package com.ledvance.database.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey
import com.ledvance.domain.bean.DeviceId

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 14:42
 * Describe : DeviceNameUpdateEntity
 */
data class DeviceNameUpdateEntity(
    @PrimaryKey
    @ColumnInfo(name = "device_id")
    val deviceId: DeviceId,
    @ColumnInfo(name = "name")
    val name: String,
)