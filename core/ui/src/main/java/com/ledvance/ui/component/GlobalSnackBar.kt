package com.ledvance.ui.component

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import com.ledvance.ui.theme.LocalSnackBarHostState

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/9/9 14:16
 * Describe : ShowGlobalSnackBar
 */
@Composable
fun rememberSnackBarState(): SnackbarHostState {
    return LocalSnackBarHostState.current
}

suspend fun SnackbarHostState.showToast(
    message: String,
    actionLabel: String? = null,
    withDismissAction: Boolean = false,
    duration: SnackbarDuration =
        if (actionLabel == null) SnackbarDuration.Short else SnackbarDuration.Indefinite
) {
    currentSnackbarData?.dismiss()
    showSnackbar(message, actionLabel, withDismissAction, duration)
}

suspend fun SnackbarHostState.checkShowToast(result: Result<*>): Boolean {
    val errorMsg = result.exceptionOrNull()?.message
    if (!errorMsg.isNullOrEmpty()) {
        showToast(errorMsg)
        return true
    }
    return false
}