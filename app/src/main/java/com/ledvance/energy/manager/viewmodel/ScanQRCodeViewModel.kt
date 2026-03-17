package com.ledvance.energy.manager.viewmodel

import androidx.lifecycle.ViewModel
import com.ledvance.utils.DeviceManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import timber.log.Timber
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/14/25 15:50
 * Describe : ScanQRCodeViewModel
 */
@HiltViewModel
class ScanQRCodeViewModel @Inject constructor() : ViewModel() {
    private val TAG = "ScanQRCodeViewModel"

    private val scanSNFlow = MutableStateFlow("")
    private val invalidQRCodeDialogFlow = MutableStateFlow<Boolean>(false)

    fun getScanSNFlow(): StateFlow<String> = scanSNFlow
    fun getInvalidQRCodeDialogFlow(): StateFlow<Boolean> = invalidQRCodeDialogFlow

    fun handleResult(value: String) {
        Timber.tag(TAG).i("handleResult content -> $value")
        if (invalidQRCodeDialogFlow.value) {
            return
        }
        if (!DeviceManager.isValidSN(value) || value.length != 11) {
            invalidQRCodeDialogFlow.update { true }
            return
        }
        scanSNFlow.update { value }
    }

    fun hideInvalidQRCodeDialog() {
        invalidQRCodeDialogFlow.update { false }
    }
}