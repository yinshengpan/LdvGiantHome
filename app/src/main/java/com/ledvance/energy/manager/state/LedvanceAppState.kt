package com.ledvance.energy.manager.state

import androidx.compose.material3.DrawerState
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.CoroutineScope

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/23 10:19
 * Describe : rememberLedvanceAppState
 */

@Composable
fun rememberLedvanceAppState(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    drawerState: DrawerState = rememberDrawerState(initialValue = DrawerValue.Closed),
): LedvanceAppState {
    return remember(coroutineScope, drawerState) {
        LedvanceAppState(
            coroutineScope = coroutineScope,
            drawerState = drawerState,
        )
    }
}

@Stable
class LedvanceAppState(
    val coroutineScope: CoroutineScope,
    val drawerState: DrawerState,
)