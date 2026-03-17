package com.ledvance.energy.manager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.energy.manager.repo.LicensesRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/19/25 18:30
 * Describe : LicensesViewModel
 */
@HiltViewModel
class LicensesViewModel @Inject constructor(
    private val licensesRepo: LicensesRepo
) : ViewModel() {

    val licensesFlow = licensesRepo.getLicensesFlow()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )
}