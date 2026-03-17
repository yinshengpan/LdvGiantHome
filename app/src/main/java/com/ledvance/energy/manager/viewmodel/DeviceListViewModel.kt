package com.ledvance.energy.manager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.energy.manager.utils.toScannedDevice
import com.ledvance.utils.DeviceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/17/25 17:04
 * Describe : DeviceListViewModel
 */
@HiltViewModel
class DeviceListViewModel @Inject constructor(
    private val deviceRepo: DeviceRepo
) : ViewModel() {
    val localDevices = deviceRepo.getDeviceListFlow()
        .map { it.map { it.toScannedDevice() } }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )

    fun deleteDevice(address: String) {
        viewModelScope.launch {
            deviceRepo.deleteDevice(address)
            DeviceManager.removeSN(address)
        }
    }
}