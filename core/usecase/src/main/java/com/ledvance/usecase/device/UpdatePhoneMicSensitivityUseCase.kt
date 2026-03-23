package com.ledvance.usecase.device

import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.SuspendUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/23/26 11:32
 * Describe : UpdatePhoneMicSensitivityUseCase
 */
@ViewModelScoped
class UpdatePhoneMicSensitivityUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
    private val deviceRepo: DeviceRepo,
) : SuspendUseCase<UpdatePhoneMicSensitivityUseCase.Param, Unit>(dispatcher) {
    override suspend fun execute(parameter: Param) {
        with(parameter) {
            deviceRepo.updatePhoneMicSensitivity(
                deviceId = deviceId,
                phoneMicSensitivity = sensitivity,
            )
        }
    }

    data class Param(
        val deviceId: DeviceId,
        val sensitivity: Int,
    )
}