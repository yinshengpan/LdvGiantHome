package com.ledvance.utils

import android.app.Application

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/5/29 09:29
 * Describe : AppContext
 */
object AppContext {
    @Volatile
    private lateinit var context: Application

    fun get(): Application = context

    fun init(application: Application) {
        context = application
    }
}