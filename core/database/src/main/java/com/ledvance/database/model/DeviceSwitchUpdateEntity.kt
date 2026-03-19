package com.ledvance.database.model

import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 14:42
 * Describe : DeviceSwitchUpdateEntity
 */
data class DeviceSwitchUpdateEntity(
    @PrimaryKey val address: String,
    @ColumnInfo(name = "switch_state")
    val switch: Boolean,
)