package com.ledvance.usecase.device

import com.ledvance.database.repo.FirmwareLatestRepo
import com.ledvance.domain.FirmwareLatest
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.FlowUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/26/26 10:13
 * Describe : GetDeviceFirmwareLatestUseCase
 */
@ViewModelScoped
class GetDeviceFirmwareLatestUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
    private val firmwareLatestRepo: FirmwareLatestRepo
) : FlowUseCase<DeviceId, FirmwareLatest?>(dispatcher) {
    override fun execute(parameter: DeviceId): Flow<FirmwareLatest?> {
        return firmwareLatestRepo.getFirmwareLatestFlow(parameter.deviceType)
    }
}