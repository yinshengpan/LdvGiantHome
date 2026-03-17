package com.ledvance.energy.manager.extensions

import android.content.Context
import android.net.Uri
import androidx.annotation.ColorInt
import androidx.browser.customtabs.CustomTabColorSchemeParams
import androidx.browser.customtabs.CustomTabsIntent
import com.ledvance.utils.extensions.tryCatch

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/7/9 10:01
 * Describe : ChromeTabExtensions
 */
fun launchCustomChromeTab(context: Context, uri: Uri, @ColorInt toolbarColor: Int) {
    tryCatch {
        val customTabBarColor = CustomTabColorSchemeParams.Builder()
            .setToolbarColor(toolbarColor).build()
        val customTabsIntent = CustomTabsIntent.Builder()
            .setDefaultColorSchemeParams(customTabBarColor)
            .setUrlBarHidingEnabled(true)
            .setShowTitle(true)
            .build()
        customTabsIntent.launchUrl(context, uri)
    }
}