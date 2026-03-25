package com.ledvance.domain

import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/26/26 16:07
 * Describe : FirmwareVersion
 */
@OptIn(InternalSerializationApi::class)
@Serializable
data class FirmwareVersion(val value: Int, val displayName: String, val advertisedVersion: String) {
    companion object {
        val default = FirmwareVersion(-1, "unknow", "XX")

        fun create(version: Int?): FirmwareVersion {
            if (version == null || version == -1) {
                return default
            }

            val newVersion = version.coerceIn(0, 99)
            val major = newVersion / 10
            val minor = newVersion % 10
            val displayName = "v$major.$minor"
            val advertisedVersion = "%02d".format(newVersion)
            return FirmwareVersion(newVersion, displayName, advertisedVersion)
        }
    }
}