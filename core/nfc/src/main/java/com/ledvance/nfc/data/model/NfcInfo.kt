package com.ledvance.nfc.data.model

import androidx.annotation.Keep
import com.ledvance.domain.bean.Company
import com.ledvance.domain.bean.DeviceType
import com.ledvance.nfc.converter.mapping.Mapping
import kotlinx.serialization.Serializable


/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/5 17:30
 * Describe : DriverInfo
 */
@Keep
@Serializable
data class NfcInfo(
    val macAddress: String,
    val deviceType: DeviceType,
    val company: Company,
    val mapping: Mapping,
)
