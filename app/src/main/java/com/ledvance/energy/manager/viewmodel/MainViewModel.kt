package com.ledvance.energy.manager.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.log.LogManager
import com.ledvance.usecase.NfcUseCase
import com.ledvance.usecase.device.AddDeviceUseCase
import com.ledvance.usecase.device.DeviceControlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/1/26 14:56
 * Describe : MainViewModel
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val nfcUseCase: NfcUseCase,
    private val addDeviceUseCase: AddDeviceUseCase,
    private val deviceControlUseCase: DeviceControlUseCase,
) : ViewModel() {

    val nfcModel = nfcUseCase.nfcModel.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun dispatchActivityOnResume(activity: Activity, intent: Intent?) {
        nfcUseCase.dispatchActivityOnResume(activity, intent)
    }

    fun dispatchActivityOnPause(activity: Activity) {
        nfcUseCase.dispatchActivityOnPause(activity)
    }

    fun connectDevice(deviceId: DeviceId) {
        viewModelScope.launch {
            addDeviceUseCase(
                parameter = AddDeviceUseCase.Param(
                    deviceId = deviceId,
                    name = "LEDVANCE Bedside lamp",
                    firmwareVersion = 0
                )
            )
            deviceControlUseCase.asyncConnectDevice(deviceId)
        }
    }

    fun resetNfc() {
        nfcUseCase.reset()
    }

    fun release() {
        nfcUseCase.reset()
        LogManager.release()
    }
}