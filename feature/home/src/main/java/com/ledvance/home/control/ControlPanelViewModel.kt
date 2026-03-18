package com.ledvance.home.control

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.ble.usecase.DeviceUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class ControlPanelViewModel @Inject constructor(
    private val deviceUseCase: DeviceUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    val mac: String = savedStateHandle.get<String>("mac") ?: ""

    fun on() {
        viewModelScope.launch {
            try {
                deviceUseCase.on(mac)
            } catch (e: Exception) {
                Timber.e(e, "ControlPanel on failed")
            }
        }
    }

    fun off() {
        viewModelScope.launch {
            try {
                deviceUseCase.off(mac)
            } catch (e: Exception) {
                Timber.e(e, "ControlPanel off failed")
            }
        }
    }

    fun setHSV(h: Int, s: Int, v: Int) {
        viewModelScope.launch {
            try {
                deviceUseCase.setHSV(mac, h, s, v)
            } catch (e: Exception) {
                Timber.e(e, "ControlPanel setHSV failed")
            }
        }
    }

    fun setCCT(temp: Int, brightness: Int) {
        viewModelScope.launch {
            try {
                deviceUseCase.setCCT(mac, temp, brightness)
            } catch (e: Exception) {
                Timber.e(e, "ControlPanel setCCT failed")
            }
        }
    }

    fun setScene(sceneId: Int) {
        viewModelScope.launch {
            try {
                deviceUseCase.setScene(mac, sceneId)
            } catch (e: Exception) {
                Timber.e(e, "ControlPanel setScene failed")
            }
        }
    }
}
