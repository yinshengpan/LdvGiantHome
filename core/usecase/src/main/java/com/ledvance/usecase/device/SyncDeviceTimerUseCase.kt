package com.ledvance.usecase.device

import com.ledvance.ble.core.DeviceRegistry
import com.ledvance.database.model.TimerEntity
import com.ledvance.database.repo.TimerRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.UseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/20/26 16:50
 * Describe : SyncDeviceTimerUseCase — 同步内存中设备的定时器状态到数据库
 */
@ViewModelScoped
class SyncDeviceTimerUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) private val dispatcher: CoroutineDispatcher,
    private val deviceRegistry: DeviceRegistry,
    private val timerRepo: TimerRepo,
) : UseCase<SyncDeviceTimerUseCase.Param, Job>() {

    override fun execute(parameter: Param): Job {
        return deviceRegistry.devicesFlow
            .map { deviceList ->
                deviceList.filter { it.deviceId == parameter.deviceId }.flatMap { device ->
                    listOfNotNull(device.onTimer, device.offTimer)
                        .map { timer ->
                            TimerEntity(
                                deviceId = timer.deviceId,
                                timerType = timer.timerType,
                                enabled = timer.enabled,
                                hour = timer.hour,
                                minute = timer.minute,
                                weekCycle = timer.weekCycle
                            )
                        }
                }
            }
            .distinctUntilChanged()
            .onEach { entityList ->
                if (entityList.isNotEmpty()) {
                    timerRepo.upsertTimers(entityList)
                }
            }
            .flowOn(dispatcher)
            .launchIn(parameter.scope)
    }

    data class Param(
        val deviceId: DeviceId,
        val scope: CoroutineScope,
    )
}
