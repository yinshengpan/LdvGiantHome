package com.ledvance.nfc.utils

import android.os.Build

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 10/31/25 09:57
 * Describe : DeviceUtils
 */
object DeviceUtils {
    fun isSamsungDevice(): Boolean {
        val manufacturer = Build.MANUFACTURER?.lowercase() ?: return false
        val brand = Build.BRAND?.lowercase() ?: ""
        val model = Build.MODEL?.lowercase() ?: ""
        return "samsung" in manufacturer || "samsung" in brand || "samsung" in model
    }
}