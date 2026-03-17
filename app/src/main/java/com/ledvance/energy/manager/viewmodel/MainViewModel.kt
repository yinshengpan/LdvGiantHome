package com.ledvance.energy.manager.viewmodel

import android.app.Activity
import android.content.Intent
import androidx.lifecycle.ViewModel
import com.ledvance.log.LogManager
import com.ledvance.nfc.data.repository.NfcDataRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/20/25 11:38
 * Describe : MainViewModel
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val nfcRepository: NfcDataRepository
) : ViewModel() {

    init {
        nfcRepository.disableNfcReaderOrWriter()
    }

    fun dispatchActivityOnResume(activity: Activity, intent: Intent?) {
        nfcRepository.dispatchActivityOnResume(activity, intent)
    }

    fun dispatchActivityOnPause(activity: Activity) {
        nfcRepository.dispatchActivityOnPause(activity)
    }

    private fun release() {
        nfcRepository.reset()
    }

    override fun onCleared() {
        super.onCleared()
        release()
    }
}