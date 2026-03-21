package com.ledvance.usecase.device

import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.UseCase
import com.ledvance.utils.extensions.toTimeInfo
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import java.time.LocalDateTime
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 10:52
 * Describe : SyncDeviceTimeUseCase
 */
@ViewModelScoped
class SetDeviceTimeUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) private val dispatcher: CoroutineDispatcher,
    private val deviceControlUseCase: DeviceControlUseCase,
) : UseCase<SetDeviceTimeUseCase.Param, Unit>() {
    override fun execute(parameter: Param) {
        val timeInfo = LocalDateTime.now().toTimeInfo()
    }

    data class Param(
        val deviceId: DeviceId,
        val hour: Int,
        val minutes: Int,
        val repeat: Int,
    )

}