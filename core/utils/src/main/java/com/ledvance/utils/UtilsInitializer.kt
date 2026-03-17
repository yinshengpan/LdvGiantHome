package com.ledvance.utils

import android.content.Context
import androidx.startup.Initializer

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/4 11:23
 * Describe : UtilsInitializer
 */
class UtilsInitializer : Initializer<Boolean> {

    override fun create(context: Context): Boolean {
        Storage.initialize(context)
        BluetoothManager.initialize()
        LocationManager.initialize()
        return true
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}