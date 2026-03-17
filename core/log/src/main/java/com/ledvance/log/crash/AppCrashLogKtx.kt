package com.ledvance.log.crash

import android.util.Log
import com.ledvance.log.LogManager

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/12/7 14:43
 * Describe : AppCrashLogKtx
 */
internal fun registerAppCrashLog() {
    val systemUncaughtExceptionHandler = Thread.getDefaultUncaughtExceptionHandler()
    Thread.setDefaultUncaughtExceptionHandler { t, e ->
        LogManager.writeLog(Log.ERROR, "FATAL-ERROR", e.stackTraceToString())
        LogManager.flushLog()
        systemUncaughtExceptionHandler?.uncaughtException(t, e)
    }
}