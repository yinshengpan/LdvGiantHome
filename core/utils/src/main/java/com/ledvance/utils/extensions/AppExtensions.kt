package com.ledvance.utils.extensions

import android.content.Context
import android.content.pm.PackageInfo
import android.content.pm.PackageManager

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/24/25 10:16
 * Describe : AppExtensions
 */
fun Context.getAppName(): String? {
    return tryCatchReturn {
        getPkgInfo()?.applicationInfo?.let {
            packageManager?.getApplicationLabel(it)?.toString()
        }
    }
}

fun Context.getVersionName(): String {
    return tryCatchReturn { getPkgInfo()?.versionName } ?: ""
}

fun Context.getVersionCode(): String {
    return tryCatchReturn { getPkgInfo()?.longVersionCode?.toString() } ?: ""
}

fun Context.getFirstInstallTime(): Long? {
    return tryCatchReturn { getPkgInfo()?.firstInstallTime }
}

fun Context.getLastUpdateTime(): Long? {
    return tryCatchReturn { getPkgInfo()?.lastUpdateTime }
}

fun Context.getPkgInfo(flags: Int = 0): PackageInfo? {
    return tryCatchReturn { packageManager?.getPackageInfo(packageName, flags) }
}

fun Context.getMetaData(key: String): String? {
    return tryCatchReturn {
        packageManager?.getApplicationInfo(
            packageName,
            PackageManager.GET_META_DATA
        )?.metaData?.getString(key)
    }
}