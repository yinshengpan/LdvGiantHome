package com.ledvance.usecase.device

import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.FlowUseCaseWithoutParameter
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 16:05
 * Describe : GetAllDeviceIdUseCase
 */
@ViewModelScoped
class GetAllDeviceIdUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
    private val deviceRepo: DeviceRepo
) : FlowUseCaseWithoutParameter<List<DeviceId>>(dispatcher) {
    override fun execute(parameter: Unit): Flow<List<DeviceId>> {
        return deviceRepo.getDeviceIdListFlow().distinctUntilChanged()
    }
}
