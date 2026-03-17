package com.ledvance.energy.manager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.ble.DLBBleUseCase
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.network.repo.FirmwareRepo
import com.ledvance.utils.extensions.sizeString
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import kotlin.math.roundToInt

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/18/25 18:10
 * Describe : FirmwareUpdateViewModel
 */
@HiltViewModel
class FirmwareUpdateViewModel @Inject constructor(
    private val deviceRepo: DeviceRepo,
    private val firmwareRepo: FirmwareRepo,
    private val dlbBleUseCase: DLBBleUseCase,
) : ViewModel() {
    private val TAG = "FirmwareUpdateViewModel"
    private val firmwareStateFlow = MutableStateFlow<FirmwareUpdateState?>(null)

    @Volatile
    private var isUpdating: Boolean = false

    fun getFirmwareUIStateFlow(): StateFlow<FirmwareUpdateState?> = firmwareStateFlow

    init {
        viewModelScope.launch(Dispatchers.IO) {
            firmwareRepo.syncFirmware()
        }

        viewModelScope.launch(Dispatchers.IO) {
            val device = dlbBleUseCase.getDevice() ?: return@launch firmwareStateFlow.update {
                FirmwareUpdateState.NoUpdateAvailable()
            }
            val localDevice =
                deviceRepo.getDevice(device.address) ?: return@launch firmwareStateFlow.update {
                    FirmwareUpdateState.NoUpdateAvailable()
                }
            val cloudFirmwareVersion = firmwareRepo.getCloudFirmwareVersion()
            val otaFile = firmwareRepo.getOtaFile() ?: return@launch firmwareStateFlow.update {
                FirmwareUpdateState.NoUpdateAvailable(curVersion = localDevice.firmwareVersion)
            }
            val curVersion = localDevice.firmwareVersion.uppercase()
            val newVersion = cloudFirmwareVersion.uppercase()
            Timber.tag(TAG).i("init curVersion:$curVersion,newVersion:$newVersion")
            firmwareStateFlow.update {
                if (newVersion == curVersion) {
                    FirmwareUpdateState.NoUpdateAvailable(curVersion = curVersion)
                } else {
                    FirmwareUpdateState.FirmwareInfo(
                        curVersion = curVersion,
                        newVersion = newVersion,
                        newOtaFileSize = otaFile.sizeString()
                    )
                }
            }
        }
    }

    fun startUpdateFirmware() {
        viewModelScope.launch(Dispatchers.IO) {
            Timber.tag(TAG).i("startUpdateFirmware begin")
            dlbBleUseCase.getDevice() ?: return@launch let {
                firmwareStateFlow.update { FirmwareUpdateState.UpdateFailed }
            }
            val cloudVersion = firmwareRepo.getCloudFirmwareVersion().uppercase()
            val otaFile = firmwareRepo.getOtaFile() ?: return@launch let {
                firmwareStateFlow.update { FirmwareUpdateState.UpdateFailed }
            }
            firmwareStateFlow.update { FirmwareUpdateState.UpdateProgress(0) }
            Timber.tag(TAG).i("startUpdateFirmware sendFile ${otaFile.absolutePath}")
            isUpdating = true
            val isSuccessfully = dlbBleUseCase.sendFile(otaFile) { progress, total ->
                firmwareStateFlow.update {
                    FirmwareUpdateState.UpdateProgress((progress * 100f / total).roundToInt())
                }
            }
            isUpdating = false
            Timber.tag(TAG).i("startUpdateFirmware sendFile isSuccessfully:$isSuccessfully")
            if (isSuccessfully) {
                firmwareStateFlow.update { FirmwareUpdateState.NoUpdateAvailable(cloudVersion) }
            } else {
                firmwareStateFlow.update { FirmwareUpdateState.UpdateFailed }
            }
        }
    }

    fun isUpdateSuccess(): Boolean {
        return dlbBleUseCase.getDevice() == null
    }

    fun isUpdating(): Boolean {
        return isUpdating
    }
}

sealed interface FirmwareUpdateState {
    data class UpdateProgress(val progress: Int) : FirmwareUpdateState
    data object UpdateFailed : FirmwareUpdateState
    data class NoUpdateAvailable(val curVersion: String = "") : FirmwareUpdateState
    data class FirmwareInfo(
        val curVersion: String,
        val newVersion: String,
        val newOtaFileSize: String
    ) : FirmwareUpdateState
}