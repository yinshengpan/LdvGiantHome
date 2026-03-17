package com.ledvance.energy.manager.navigation

import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalFocusManager
import com.ledvance.ui.component.Keyboard
import com.ledvance.ui.component.keyboardAsState

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/14/25 14:43
 * Describe : MainNavigationScaffold
 */
@Composable
fun MainNavigationScaffold(
    bottomBar: @Composable () -> Unit = {},
    snackbarHost: @Composable () -> Unit = {},
    content: @Composable (PaddingValues) -> Unit,
) {
    val localFocusManager = LocalFocusManager.current
    val keyBoardState by keyboardAsState()
    LaunchedEffect(keyBoardState) {
        if (keyBoardState == Keyboard.Closed) {
            localFocusManager.clearFocus()
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxSize()
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    localFocusManager.clearFocus()
                })
            },
    ) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = bottomBar,
            snackbarHost = snackbarHost,
            content = content
        )
    }
}