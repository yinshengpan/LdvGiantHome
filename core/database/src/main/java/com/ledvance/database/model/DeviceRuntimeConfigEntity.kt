package com.ledvance.database.model

import androidx.room.*
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.WorkMode
import com.ledvance.domain.bean.command.giant.LineSequence

@Entity(
    tableName = "device_runtime_config",
    foreignKeys = [
        ForeignKey(
            entity = DeviceEntity::class,
            parentColumns = ["device_id"],
            childColumns = ["device_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class DeviceRuntimeConfigEntity(
    @PrimaryKey
    @ColumnInfo(name = "device_id")
    val deviceId: DeviceId,
    @ColumnInfo(name = "work_mode")
    val workMode: WorkMode = WorkMode.Colour,
    @ColumnInfo(name = "line_sequence")
    val lineSequence: LineSequence = LineSequence.RGB,
    @ColumnInfo(name = "phone_mic_sensitivity")
    val phoneMicSensitivity: Int = 60,
)
