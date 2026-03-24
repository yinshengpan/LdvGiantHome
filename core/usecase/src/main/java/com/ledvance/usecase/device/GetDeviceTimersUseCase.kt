package com.ledvance.usecase.device

import android.annotation.SuppressLint
import com.ledvance.database.repo.TimerRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.TimerUiItem
import com.ledvance.domain.bean.command.timer.toTimerRepeat
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.ui.extensions.toDisplayText
import com.ledvance.usecase.base.FlowUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 15:30
 * Describe : GetDeviceTimersUseCase
 */
@ViewModelScoped
class GetDeviceTimersUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
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
