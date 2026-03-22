package com.ledvance.usecase.device

import com.ledvance.ble.core.ConnectionManager
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.usecase.base.SuspendUseCase
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import timber.log.Timber
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/21 22:00
 * Describe : DeleteDeviceUseCase
 */
@ViewModelScoped
class DeleteDeviceUseCase @Inject constructor(
    @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
    private val deviceRepo: DeviceRepo,
    private val connectionManager: ConnectionManager
) : SuspendUseCase<DeviceId, Unit>(dispatcher) {
    override suspend fun execute(parameter: DeviceId) {
        Timber.tag("DeleteDeviceUseCase").d("Executing delete for $parameter")
        // 1. Disconnect the device if it's connected
        connectionManager.disconnect(parameter)
        // 2. Wait a bit for the BLE stack to physically tear down the connection
        // and for the device to start advertising again.
        delay(1000) 
        // 3. Delete from database
        deviceRepo.deleteDevice(parameter)
        Timber.tag("DeleteDeviceUseCase").d("Successfully deleted $parameter from DB")
    }
}
