package com.ledvance.meluce.initializer

import android.content.Context
import androidx.startup.Initializer
import androidx.work.ExistingWorkPolicy
import androidx.work.WorkManager
import androidx.work.WorkManagerInitializer
import com.ledvance.meluce.workers.DataSyncWorker

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/18/25 18:23
 * Describe : SyncInitializer
 */
class SyncInitializer : Initializer<Boolean> {
    override fun create(context: Context): Boolean {
        WorkManager.getInstance(context).enqueueUniqueWork(
            uniqueWorkName = "SyncData",
            existingWorkPolicy = ExistingWorkPolicy.KEEP,
            request = DataSyncWorker.Companion.startUpSyncWork()
        )
        return true
    }

    override fun dependencies(): List<Class<out Initializer<*>?>?> {
        return listOf(WorkManagerInitializer::class.java)
    }
}