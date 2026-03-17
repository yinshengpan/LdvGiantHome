package com.ledvance.energy.manager.initializer

import android.content.Context
import androidx.startup.Initializer
import com.ledvance.energy.manager.utils.LanguageUtils
import com.ledvance.utils.UtilsInitializer

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/13/25 15:04
 * Describe : LanguageInitializer
 */
class LanguageInitializer : Initializer<Boolean> {
    override fun create(context: Context): Boolean {
        LanguageUtils.initLanguageTag()
        return true
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return listOf(UtilsInitializer::class.java)
    }
}