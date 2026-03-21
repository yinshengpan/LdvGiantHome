package com.ledvance.usecase.device

import com.ledvance.database.model.TimerEntity
import com.ledvance.database.repo.TimerRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.TimerUiItem
import com.ledvance.domain.bean.command.timer.TimerRepeat
import com.ledvance.domain.bean.command.timer.toByte
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.SuspendUseCase
import com.ledvance.utils.extensions.toBinary8
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import timber.log.Timber
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 15:35
 * Describe : UpdateDeviceTimerUseCase
 */
@ViewModelScoped
class UpdateDeviceTimerUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
    private val timerRepo: TimerRepo,
    private val deviceControlUseCase: DeviceControlUseCase
) : SuspendUseCase<UpdateDeviceTimerUseCase.Param, Boolean>(dispatcher) {
    private val TAG = "UpdateDeviceTimerUseCas"
    override suspend fun execute(parameter: Param): Boolean {
        with(parameter) {
            val weekCycle = TimerRepeat(enabled = timer.enabled, days = timer.days.toSet()).toByte().toInt() and 0xFF
            Timber.tag(TAG).d("execute() $deviceId ${weekCycle.toBinary8()}")
            timerRepo.upsertTimer(
                TimerEntity(
                    deviceId = deviceId,
                    timerType = timer.timerType,
                    enabled = timer.enabled,
                    hour = timer.hour,
                    minute = timer.minute,
                    weekCycle = weekCycle
                )
            )

            return deviceControlUseCase.setTimer(
                deviceId = deviceId,
                timerType = timer.timerType,
                hour = timer.hour,
                min = timer.minute,
                weekCycle = weekCycle,
            )
        }
    }

    data class Param(
        val deviceId: DeviceId,
        val timer: TimerUiItem,
    )
}
