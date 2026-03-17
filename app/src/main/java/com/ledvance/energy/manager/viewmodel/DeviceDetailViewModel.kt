package com.ledvance.energy.manager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.database.repo.ChargerRepo
import com.ledvance.database.repo.DeviceRepo
import com.ledvance.database.repo.SetTripCurrentHistoryRepo
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn

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
}