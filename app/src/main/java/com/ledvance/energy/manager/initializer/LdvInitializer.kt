package com.ledvance.energy.manager.initializer

import android.content.Context
import androidx.startup.Initializer

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/13/25 15:15
 * Describe : LdvInitializer
 */
class LdvInitializer : Initializer<Boolean> {
    override fun create(context: Context): Boolean {
        return true
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return listOf(
//            FirebaseInitializer::class.java,
//            LanguageInitializer::class.java,
            DarkThemeInitializer::class.java,
            SyncInitializer::class.java
        )
    }

}