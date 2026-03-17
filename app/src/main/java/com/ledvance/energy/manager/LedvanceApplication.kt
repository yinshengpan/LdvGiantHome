package com.ledvance.energy.manager

import android.app.Application
import android.content.Context
import com.ledvance.utils.AppContext
import com.ledvance.utils.extensions.enableStrictModePolicy
import com.ledvance.utils.extensions.enableTimerDebugTree
import dagger.hilt.android.HiltAndroidApp

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/5/27 13:38
 * Describe : LedvanceApplication
 */
@HiltAndroidApp
class LedvanceApplication : Application() {
    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        AppContext.init(this)
        enableTimerDebugTree()
    }

    override fun onCreate() {
        super.onCreate()
        enableStrictModePolicy()
    }

}