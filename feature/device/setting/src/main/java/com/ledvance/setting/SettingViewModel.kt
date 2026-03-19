package com.ledvance.setting

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : SettingViewModel
 */
@HiltViewModel
internal class SettingViewModel @Inject constructor() : ViewModel(), SettingContract {
    override val uiState: StateFlow<SettingContract.UiState>
        get() = TODO("Not yet implemented")
}