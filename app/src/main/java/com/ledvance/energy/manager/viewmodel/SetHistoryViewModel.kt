package com.ledvance.energy.manager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.database.repo.SetTripCurrentHistoryRepo
import com.ledvance.utils.extensions.toTimeStr
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 17:53
 * Describe : SetHistoryViewModel
 */
@HiltViewModel
class SetHistoryViewModel @Inject constructor(
    private val setTripCurrentHistoryRepo: SetTripCurrentHistoryRepo
) : ViewModel() {
    val historyListFlow = setTripCurrentHistoryRepo.getHistoryListFlow()
        .map { list ->
            list.onEach { item ->
                item.createTimeStr = item.createTime.toTimeStr()
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = listOf()
        )
}