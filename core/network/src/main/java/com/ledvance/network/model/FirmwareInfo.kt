package com.ledvance.network.model

import com.ledvance.domain.bean.DeviceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/19/25 10:46
 * Describe : FirmwareInfo
 */
@OptIn(kotlinx.serialization.InternalSerializationApi::class)
@Serializable
internal data class FirmwareInfo(
    @SerialName("fw_version") val fwVersion: String,
    @SerialName("file_url") val fileUrl: String,
    val type: FirmwareType? = null,
    val md5: String
)

@Serializable
internal enum class FirmwareType {

    @SerialName("table")
    TABLE,

    @SerialName("floor")
    FLOOR,

    @SerialName("unknown")
    UNKNOWN,
}

internal fun FirmwareType.toDeviceType(): DeviceType? {
    return when (this) {
        FirmwareType.TABLE -> DeviceType.GiantTable
        FirmwareType.FLOOR -> DeviceType.GiantFloor
        FirmwareType.UNKNOWN -> null
    }
}