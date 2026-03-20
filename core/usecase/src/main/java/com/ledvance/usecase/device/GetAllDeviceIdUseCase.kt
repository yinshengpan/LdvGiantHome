package com.ledvance.usecase.device

import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.usecase.base.FlowUseCaseWithoutParameter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:05
 * Describe : GetAllDeviceIdUseCase
 */
class GetAllDeviceIdUseCase(
    dispatcher: CoroutineDispatcher,
    private val deviceRepo: DeviceRepo
) : FlowUseCaseWithoutParameter<List<DeviceId>>(dispatcher) {
    override fun execute(parameter: Unit): Flow<List<DeviceId>> {
        return deviceRepo.getDeviceIdListFlow().distinctUntilChanged()
    }
}
