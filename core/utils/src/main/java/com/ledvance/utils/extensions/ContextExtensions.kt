package com.ledvance.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.pm.ApplicationInfo
import android.os.StrictMode
import android.os.StrictMode.ThreadPolicy.Builder
import androidx.core.view.WindowCompat
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/13 11:07
 * Describe : ContextExtensions
 */
/**
 * Return true if the application is debuggable.
 */
fun Context.isDebuggable(): Boolean {
    return 0 != applicationInfo.flags and ApplicationInfo.FLAG_DEBUGGABLE
}

/**
 * Set a thread policy that detects all potential problems on the main thread, such as network
 * and disk access.
 *
 * If a problem is found, the offending call will be logged and the application will be killed.
 */
fun Context.enableStrictModePolicy() {
    if (isDebuggable()) {
        StrictMode.setThreadPolicy(
            Builder().detectAll().penaltyLog().build(),
        )
    }
}


fun Context.enableTimerDebugTree() {
    if (isDebuggable()) {
        Timber.plant(Timber.DebugTree())
    }
}

fun Activity.setStatusBarsIcons(darkIcons: Boolean) {
    WindowCompat.getInsetsController(window, window.decorView).apply {
        isAppearanceLightStatusBars = darkIcons
    }
}
