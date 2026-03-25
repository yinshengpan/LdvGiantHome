package com.ledvance.usecase.device

import com.ledvance.database.repo.FirmwareLatestRepo
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.network.repo.FirmwareNetworkRepo
import com.ledvance.usecase.base.SuspendUseCaseWithoutParameter
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/25/26 18:19
 * Describe : FirmwareLatestSyncUseCase
 */
class SyncFirmwareLatestUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
    private val firmwareLatestRepo: FirmwareLatestRepo,
    private val firmwareNetworkRepo: FirmwareNetworkRepo,
) : SuspendUseCaseWithoutParameter<Unit>(dispatcher) {
    override suspend fun execute(parameter: Unit) {
        firmwareNetworkRepo.syncFirmwares().let {
            firmwareLatestRepo.addFirmwareLatestList(it)
        }
    }
}