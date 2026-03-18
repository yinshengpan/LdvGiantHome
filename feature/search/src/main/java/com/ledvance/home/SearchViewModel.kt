package com.ledvance.home

import android.annotation.SuppressLint
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.ble.usecase.BleSearchUseCase
import com.ledvance.utils.extensions.tryCatch
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : SearchViewModel
 */
@HiltViewModel
internal class SearchViewModel @Inject constructor(
    private val bleSearchUseCase: BleSearchUseCase
) : ViewModel(), SearchContract {

    override val uiState: StateFlow<SearchContract.UiState> =
        bleSearchUseCase.scanDeviceListFlow.map {
            if (it.isEmpty()) {
                SearchContract.UiState.Loading
            } else {
                SearchContract.UiState.Success(devices = it)
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = SearchContract.UiState.Loading
        )

    @SuppressLint("MissingPermission")
    override fun startBleScan() {
        tryCatch {
            bleSearchUseCase.startScan()
        }
    }

    override fun stopBleScan() {
        bleSearchUseCase.stopBleScan()
    }

    override fun onCleared() {
        super.onCleared()
        bleSearchUseCase.stopBleScan()
    }
}