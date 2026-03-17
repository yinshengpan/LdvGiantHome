package com.ledvance.energy.manager.workers

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.OutOfQuotaPolicy
import androidx.work.WorkerParameters
import com.ledvance.energy.manager.repo.LicensesRepo
import com.ledvance.network.repo.FirmwareRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/30 15:53
 * Describe : DataSyncWorker
 */
@HiltWorker
internal class DataSyncWorker @AssistedInject constructor(
    @Assisted private val appContext: Context,
    @Assisted workerParameters: WorkerParameters,
    private val firmwareRepo: FirmwareRepo,
    private val licensesRepo: LicensesRepo,
) : CoroutineWorker(appContext, workerParameters) {

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return appContext.syncForegroundInfo()
    }

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        awaitAll(
            async {
                licensesRepo.syncLicenses()
            },
            async {
                firmwareRepo.syncFirmware()
            }
        )
        return@withContext Result.success()
    }

    companion object {
        fun startUpSyncWork() = OneTimeWorkRequestBuilder<DelegatingWorker>()
            .setExpedited(OutOfQuotaPolicy.RUN_AS_NON_EXPEDITED_WORK_REQUEST)
            .setConstraints(SyncConstraints)
            .setInputData(DataSyncWorker::class.delegatedData())
            .build()
    }
}