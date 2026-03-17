package com.ledvance.log

import android.content.Context
import java.io.File

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/23 08:56
 * Describe : LogManager
 */
object LogManager {
    suspend fun shareAppLog(context: Context, title: String, email: String) {
    }

    suspend fun getLogZipFile(context: Context): File? = null

    fun release() {}
}