package com.ledvance.ui.component

import androidx.annotation.StringRes
import com.ledvance.ui.R
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/22 09:05
 * Describe : Global Snackbar Manager singleton for triggering snackbars from anywhere.
 */
object SnackbarManager {
    private val _messages = MutableSharedFlow<SnackbarMessage>()
    val messages = _messages.asSharedFlow()

    suspend fun showMessage(message: String) {
        _messages.emit(SnackbarMessage.Text(message))
    }

    suspend fun showMessage(@StringRes messageRes: Int) {
        _messages.emit(SnackbarMessage.Resource(messageRes))
    }

    suspend fun showGenericError() {
        showMessage(R.string.command_failed)
    }
}

sealed class SnackbarMessage {
    data class Text(val value: String) : SnackbarMessage()
    data class Resource(@StringRes val resId: Int) : SnackbarMessage()
}

