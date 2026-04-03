package com.ledvance.room

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : RoomViewModel
 */
@HiltViewModel
internal class RoomViewModel @Inject constructor() : ViewModel(), RoomContract {
    override val uiState: StateFlow<RoomContract.UiState> = MutableStateFlow(RoomContract.UiState.Success)
}