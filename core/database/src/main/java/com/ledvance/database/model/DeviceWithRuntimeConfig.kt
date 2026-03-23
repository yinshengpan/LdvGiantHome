package com.ledvance.database.model

import androidx.room.Embedded
import androidx.room.Relation

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/03/23 17:10
 * Describe : DeviceWithRuntimeConfig - Join between DeviceEntity and DeviceConfigEntity
 */
data class DeviceWithRuntimeConfig(
    @Embedded val device: DeviceEntity,
    @Relation(
        parentColumn = "device_id",
        entityColumn = "device_id"
    )
    val config: DeviceRuntimeConfigEntity?
)
