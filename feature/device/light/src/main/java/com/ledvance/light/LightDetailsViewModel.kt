package com.ledvance.light

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : LightDetailsViewModel
 */
@HiltViewModel
internal class LightDetailsViewModel @Inject constructor() : ViewModel(), LightDetailsContract {
    override val uiState: StateFlow<LightDetailsContract.UiState>
        get() = TODO("Not yet implemented")
}