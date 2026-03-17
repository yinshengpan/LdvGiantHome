package com.ledvance.energy.manager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.database.model.SetTripCurrentType
import com.ledvance.database.repo.ChargerRepo
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.database.repo.SetTripCurrentHistoryRepo
import com.ledvance.nfc.data.model.DriverModel
import com.ledvance.nfc.data.model.EVChargerParam
import com.ledvance.nfc.data.repository.NfcDataRepository
import com.ledvance.nfc.utils.NfcProgressState
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/18/25 10:16
 * Describe : DeviceDetailViewModel
 */
@AssistedFactory
interface DeviceDetailFactory {
    fun create(address: String): DeviceDetailViewModel
}

@HiltViewModel(assistedFactory = DeviceDetailFactory::class)
class DeviceDetailViewModel @AssistedInject constructor(
    @Assisted private val address: String,
    private val deviceRepo: DeviceRepo,
    private val chargerRepo: ChargerRepo,
    private val setTripCurrentHistoryRepo: SetTripCurrentHistoryRepo,
    private val nfcRepository: NfcDataRepository
) : ViewModel() {
    private var tripCurrent: String? = null
    val device = deviceRepo.getDeviceFlow(address)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val chargerList = chargerRepo.getAllChargerListFlow(address)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )

    val writingProgress = nfcRepository.writingProgress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NfcProgressState.Idle
    )

    fun updateLocalTripCurrentByNFC(device: ScannedDevice) {
        viewModelScope.launch {
            val current = tripCurrent?.toIntOrNull() ?: return@launch
            deviceRepo.updateTripCurrent(address, current)
            setTripCurrentHistoryRepo.addHistory(device.sn, current, SetTripCurrentType.Nfc)
            tripCurrent = null
        }
    }

    fun updateNFCTripCurrent(tripCurrent: String) {
        val model = DriverModel.EmptyChargerModel
        val evChargerParam = EVChargerParam(
            sn = "",
            tripCurrent = tripCurrent
        )
        this.tripCurrent = tripCurrent
        nfcRepository.updateDriverModel(model.copy(evChargerParam = evChargerParam))
    }

    val nfcEnable: StateFlow<Boolean>
        get() = nfcRepository.nfcEnable

    fun showWriteDriverDialog() {
        nfcRepository.enableNfcWriter()
    }

    fun hideReadOrWriteDriverDialog() {
        nfcRepository.reset()
        nfcRepository.disableNfcReaderOrWriter()
    }

    fun hasSupportNfc(): Boolean {
        return nfcRepository.hasSupportNfc()
    }

    override fun onCleared() {
        super.onCleared()
        nfcRepository.reset()
        nfcRepository.disableNfcReaderOrWriter()
    }
}