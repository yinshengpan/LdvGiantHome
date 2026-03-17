package com.ledvance.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/19/25 10:46
 * Describe : FirmwareInfo
 */
@Serializable
data class FirmwareInfo(
    @SerialName("fw_version") val fwVersion: String,
    @SerialName("file_url") val fileUrl: String,
    val md5: String
)