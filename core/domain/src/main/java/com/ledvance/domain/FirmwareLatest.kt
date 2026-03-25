package com.ledvance.domain

import com.ledvance.domain.bean.DeviceType

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/25/26 18:03
 * Describe : FirmwareLatest
 */
data class FirmwareLatest(
    val deviceType: DeviceType,
    val latestVersion: FirmwareVersion,
    val firmwareFilePath: String,
    val firmwareFileSize: Long,
    val firmwareMd5: String,
    val firmwareUrl: String,
)