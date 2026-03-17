package com.ledvance.connected.system.extensions

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Parcelable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/30 09:46
 * Describe : IntentExtensions
 */
inline fun <reified T : Parcelable> Intent.getParcelable(key: String): T? {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        getParcelableExtra(key, T::class.java)
    } else {
        getParcelableExtra(key)
    }
}

fun Intent.resolveActivity(context: Context) =
    context.packageManager?.resolveActivity(this, PackageManager.MATCH_DEFAULT_ONLY) != null

fun Intent.checkActivityContext(context: Context) {
    if (context !is Activity) {
        this.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
}