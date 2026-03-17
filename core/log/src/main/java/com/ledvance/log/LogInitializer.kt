package com.ledvance.log

import android.content.Context
import androidx.startup.Initializer

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/4 11:23
 * Describe : LogInitProvider
 */
internal class LogInitializer : Initializer<Boolean> {

    override fun create(context: Context): Boolean {
        LogManager.initialize(context)
        return true
    }

    override fun dependencies(): List<Class<out Initializer<*>>> {
        return emptyList()
    }
}