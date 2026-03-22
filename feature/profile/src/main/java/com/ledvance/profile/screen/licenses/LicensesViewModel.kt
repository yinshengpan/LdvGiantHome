package com.ledvance.profile.screen.licenses

import com.ledvance.usecase.repo.LicensesRepo

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : LicensesViewModel
 */
@HiltViewModel
internal class LicensesViewModel @Inject constructor(
    private val licensesRepo: LicensesRepo
) : ViewModel(), LicensesContract {

    override val uiState: StateFlow<LicensesContract.UiState> = licensesRepo.getLicensesFlow()
        .map { licenses ->
            if (licenses.isEmpty()) {
                LicensesContract.UiState.Loading
            } else {
                LicensesContract.UiState.Success(licenses)
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LicensesContract.UiState.Loading
        )

    init {
        onRefresh()
    }

    override fun onRefresh() {
        viewModelScope.launch {
            licensesRepo.syncLicenses()
        }
    }
}
