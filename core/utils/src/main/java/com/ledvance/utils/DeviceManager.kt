package com.ledvance.utils

import com.tencent.mmkv.MMKV

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/14/25 16:14
 * Describe : DeviceManager
 */
object DeviceManager {

    private val snMmkv by lazy {
        MMKV.mmkvWithID("device_sn")
    }

    fun getSN(address: String): String {
        return snMmkv.getString(address, "") ?: ""
    }

    private val snRegex by lazy {
        Regex("^[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}-[0-9A-Fa-f]{2}$")
    }

    fun removeSN(address: String) {
        snMmkv.remove(address)
    }

    fun setSN(address: String, sn: String) {
        snMmkv.putString(address, sn)
    }

    fun isValidSN(sn: String): Boolean {
        return snRegex.matches(sn) && sn != "00-00-00-00"
    }
}