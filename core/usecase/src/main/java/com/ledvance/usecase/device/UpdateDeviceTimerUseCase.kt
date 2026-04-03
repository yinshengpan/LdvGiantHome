package com.ledvance.usecase.device

import com.ledvance.database.model.TimerEntity
import com.ledvance.database.repo.TimerRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.TimerUiItem
import com.ledvance.domain.bean.command.common.TimerRepeat
import com.ledvance.domain.bean.command.common.toGiantByte
import com.ledvance.domain.bean.command.common.toLdvByte
import com.ledvance.domain.bean.isGiantTimer
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.SuspendUseCase
import com.ledvance.utils.extensions.toUnsignedInt
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
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
    override suspend fun execute(parameter: Param): Boolean {
        with(parameter) {
            val timerRepeat = TimerRepeat(enabled = timer.enabled, days = timer.days.toSet())
            val weekCycle = if (timer.isGiantTimer()) {
                timerRepeat.toGiantByte().toUnsignedInt()
            } else {
                timerRepeat.toLdvByte().toUnsignedInt()
            }
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
                delay = timer.delay,
                timerRepeat = timerRepeat,
            )
        }
    }

    data class Param(
        val deviceId: DeviceId,
        val timer: TimerUiItem,
    )
}
