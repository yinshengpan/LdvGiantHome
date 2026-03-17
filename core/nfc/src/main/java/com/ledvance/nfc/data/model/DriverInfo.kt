package com.ledvance.nfc.data.model

import androidx.annotation.Keep
import kotlinx.serialization.Serializable


/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/5 17:30
 * Describe : DriverInfo
 */
@Keep
@Serializable
data class DriverInfo(
    val driverType: Int,
    val mapping: Int,
    val nfcDataBase64: String
)