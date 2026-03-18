package com.ledvance.database.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ledvance.domain.bean.DeviceType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/23 17:57
 * Describe : DeviceEntity
 */
@Entity(tableName = "devices")
data class DeviceEntity(
    @PrimaryKey
    val address: String,
    val name: String,
    val deviceType: DeviceType,
    val switch: Boolean = true,
)