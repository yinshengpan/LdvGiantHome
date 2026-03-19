package com.ledvance.scenes

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : ScenesViewModel
 */
@HiltViewModel
internal class ScenesViewModel @Inject constructor() : ViewModel(), ScenesContract {
    override val uiState: StateFlow<ScenesContract.UiState>
        get() = TODO("Not yet implemented")
}