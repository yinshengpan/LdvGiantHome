package com.ledvance.ble.usecase

import com.ledvance.domain.usecase.SuspendUseCase
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 17:50
 * Describe : DeviceSwitchUseCase
 */
class DeviceSwitchUseCase(
    dispatcher: CoroutineDispatcher,
    private val deviceUseCase: DeviceUseCase,
) : SuspendUseCase<DeviceSwitchUseCase.Param, Unit>(dispatcher) {
    override suspend fun execute(parameter: Param) {
        with(parameter) {
            Timber.d("DeviceSwitchUseCase address:$address,switch:$switch")
            if (switch) {
                deviceUseCase.on(address)
            } else {
                deviceUseCase.off(address)
            }
        }
    }

    data class Param(
        val address: String,
        val switch: Boolean
    )
}