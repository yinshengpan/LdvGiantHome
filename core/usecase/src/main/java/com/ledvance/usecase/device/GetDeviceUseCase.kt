package com.ledvance.usecase.device

import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceUiItem
import com.ledvance.usecase.base.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 15:33
 * Describe : GetDeviceUseCase
 */
class GetDeviceUseCase(
    dispatcher: CoroutineDispatcher,
    private val deviceRepo: DeviceRepo
) : FlowUseCase<String, DeviceUiItem>(dispatcher) {
    override fun execute(parameter: String): Flow<DeviceUiItem> {
        return deviceRepo.getDeviceFlow(parameter)
            .map { it?.toDeviceUiItem() }
            .filterNotNull()
            .distinctUntilChanged()
    }
}