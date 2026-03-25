package com.ledvance.usecase.device

import android.content.Context
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.domain.FirmwareVersion
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.asBluetoothDevice
import com.ledvance.domain.di.Dispatcher
import com.ledvance.domain.di.Dispatchers
import com.ledvance.ota.domain.model.OtaState
import com.ledvance.ota.domain.repository.OtaRepository
import com.ledvance.usecase.base.FlowUseCase
import com.ledvance.utils.extensions.toByteArray
import com.ledvance.utils.extensions.tryCatch
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.io.File
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/26/26 13:57
 * Describe : UpdateFirmwareUseCase
 */
@ViewModelScoped
class UpdateFirmwareUseCase @Inject constructor(
    @param:ApplicationContext private val context: Context,
    @Dispatcher(Dispatchers.IO) dispatcher: CoroutineDispatcher,
    private val otaRepository: OtaRepository,
    private val deviceControlUseCase: DeviceControlUseCase,
    private val deviceRepo: DeviceRepo,
) : FlowUseCase<UpdateFirmwareUseCase.Param, UpdateFirmwareUseCase.OtaUpdateResult>(dispatcher) {
    private val TAG = "UpdateFirmwareUseCase"

    override fun execute(parameter: Param): Flow<OtaUpdateResult> = flow {
        val (deviceId, firmwareFile, firmwareVersion, deviceName) = parameter
        Timber.tag(TAG).d("OTA UseCase -> start ($deviceId)")

        val bluetoothDevice = deviceId.asBluetoothDevice(context)
        if (bluetoothDevice == null) {
            Timber.tag(TAG).i("bluetoothDevice is null")
            emit(OtaUpdateResult.Fail("device is not found"))
            return@flow
        }

        emit(OtaUpdateResult.Progress(5))
        deviceControlUseCase.disconnectDevice(deviceId)
        delay(300)
        emit(OtaUpdateResult.Progress(10))

        otaRepository.startOtaUpdate(context, bluetoothDevice, firmwareFile.toByteArray())
            .flowOn(kotlinx.coroutines.Dispatchers.Main)
            .collect { state ->
                Timber.tag(TAG).d("OTA UseCase -> state: $state")
                when (state) {
                    is OtaState.DeviceFound -> {
                        emit(OtaUpdateResult.Progress(20))
                    }

                    is OtaState.ConnectSuccess -> {
                        emit(OtaUpdateResult.Progress(30))
                    }

                    is OtaState.OtaProgress -> {
                        val mapped = 30 + (state.progress * 0.6f).toInt()
                        emit(OtaUpdateResult.Progress(mapped.coerceAtMost(95)))
                    }

                    is OtaState.OtaFail -> {
                        Timber.tag(TAG).e("OTA failed: ${state.error}")
                        emit(OtaUpdateResult.Fail(state.error))
                    }

                    OtaState.OtaSuccess -> {
                        emit(OtaUpdateResult.Progress(95))
                        tryCatch {
                            val advertisedVersion = firmwareVersion.advertisedVersion
                            val deviceName = deviceName.replaceRange(3, 5, advertisedVersion)
                            deviceRepo.updateDeviceFirmwareVersion(deviceId, deviceName, firmwareVersion)
                        }
                        emit(OtaUpdateResult.Progress(98))

                        deviceControlUseCase.connectDevice(deviceId)
                        emit(OtaUpdateResult.Progress(100))
                        emit(OtaUpdateResult.Success)
                    }

                    else -> {}
                }
            }
    }

    data class Param(
        val deviceId: DeviceId,
        val firmwareFile: File,
        val firmwareVersion: FirmwareVersion,
        val deviceName: String,
    )

    sealed interface OtaUpdateResult {
        data class Progress(val progress: Int) : OtaUpdateResult
        data class Fail(val error: String) : OtaUpdateResult
        data object Success : OtaUpdateResult
    }
}