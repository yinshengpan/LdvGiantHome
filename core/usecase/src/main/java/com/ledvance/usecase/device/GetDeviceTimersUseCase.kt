package com.ledvance.usecase.device

import android.annotation.SuppressLint
import com.ledvance.database.repo.TimerRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.TimerUiItem
import com.ledvance.domain.bean.command.timer.toTimerRepeat
import com.ledvance.domain.bean.toDisplayText
import com.ledvance.usecase.base.FlowUseCase
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 15:30
 * Describe : GetDeviceTimersUseCase
 */
class GetDeviceTimersUseCase(
    dispatcher: CoroutineDispatcher,
    private val timerRepo: TimerRepo,
) : FlowUseCase<DeviceId, List<TimerUiItem>>(dispatcher) {
    @SuppressLint("DefaultLocale")
    override fun execute(parameter: DeviceId): Flow<List<TimerUiItem>> {
        return timerRepo.getTimersFlow(parameter)
            .map { list ->
                list.map { timer ->
                    val days = timer.weekCycle.toByte().toTimerRepeat().days
                    TimerUiItem(
                        timerType = timer.timerType,
                        enabled = timer.enabled,
                        hour = timer.hour,
                        minute = timer.minute,
                        days = days,
                        displayTime = String.format("%02d:%02d", timer.hour, timer.minute),
                        displayRepeat = days.toDisplayText(),
                    )
                }
            }
    }
}
