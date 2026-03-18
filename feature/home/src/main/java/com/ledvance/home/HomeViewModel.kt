package com.ledvance.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ledvance.database.usecase.GetDevicesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import timber.log.Timber
import javax.inject.Inject

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : HomeViewModel
 */
@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val getDevicesUseCase: GetDevicesUseCase
) : ViewModel(), HomeContract {
    override val uiState: StateFlow<HomeContract.UiState> = getDevicesUseCase().map {
        if (it.isEmpty()) {
            HomeContract.UiState.Empty
        } else {
            HomeContract.UiState.Success(
                devices = it,
                onlineMap = mapOf()
            )
        }
    }
        .onStart {
            Timber.d("Loading home page")
        }
        .onEach {
            Timber.d("Home page id loaded")
        }
        .catch { error ->
            Timber.e(error, "Failed to load home page")
            emit(HomeContract.UiState.Empty)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = HomeContract.UiState.Loading
        )
}