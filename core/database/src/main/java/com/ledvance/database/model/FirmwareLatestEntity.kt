package com.ledvance.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ledvance.domain.FirmwareVersion
import com.ledvance.domain.bean.DeviceType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/25/26 17:45
 * Describe : FirmwareLatestEntity
 */
@Entity(tableName = "firmware_latest")
data class FirmwareLatestEntity(
    @PrimaryKey
    @ColumnInfo(name = "device_type")
    val deviceType: DeviceType,
    @ColumnInfo(name = "latest_version")
    val latestVersion: FirmwareVersion,
    @ColumnInfo(name = "firmware_url")
    val firmwareUrl: String,
    @ColumnInfo(name = "firmware_file_path")
    val firmwareFilePath: String = "",
    @ColumnInfo(name = "firmware_file_size")
    val firmwareFileSize: Long = 0,
    @ColumnInfo(name = "firmware_md5")
    val firmwareMd5: String,
)