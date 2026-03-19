package com.ledvance.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : ProfileViewModel
 */
@HiltViewModel
internal class ProfileViewModel @Inject constructor() : ViewModel(), ProfileContract {
    override val uiState: StateFlow<ProfileContract.UiState>
        get() = TODO("Not yet implemented")
}