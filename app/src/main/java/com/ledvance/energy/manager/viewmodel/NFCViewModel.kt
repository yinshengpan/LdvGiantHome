package com.ledvance.energy.manager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.nfc.data.repository.NfcDataRepository
import com.ledvance.nfc.utils.NfcProgressState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/20/25 11:41
 * Describe : NFCScanViewModel
 */
@HiltViewModel
class NFCViewModel @Inject constructor(val nfcRepository: NfcDataRepository) : ViewModel() {

    val driverModel = nfcRepository.driverModel.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val readingProgress = nfcRepository.readingProgress.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NfcProgressState.Idle
    )

    val nfcEnable: StateFlow<Boolean>
        get() = nfcRepository.nfcEnable

    fun disableNfcReaderOrWriter() {
        nfcRepository.disableNfcReaderOrWriter()
    }

    fun enableNfcReader() {
        nfcRepository.enableNfcReader()
    }

    fun hasSupportNfc(): Boolean {
        return nfcRepository.hasSupportNfc()
    }

    fun reset() {
        nfcRepository.reset()
    }

    override fun onCleared() {
        super.onCleared()
        nfcRepository.reset()
    }
}