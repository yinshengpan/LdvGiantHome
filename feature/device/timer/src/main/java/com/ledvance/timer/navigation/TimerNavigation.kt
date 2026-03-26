package com.ledvance.timer.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.timer.TimerScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : TimerNavigation
 */
@Serializable
internal data class TimerRoute(val deviceId: DeviceId) : NavigationRoute

fun SnapshotStateList<Any>.navigateToTimer(deviceId: DeviceId) {
    add(TimerRoute(deviceId))
}

fun EntryProviderScope<Any>.timerScreen(
    onBackClick: () -> Unit
) {
    entry<TimerRoute> {
        PageLifecycleLogger("TimerRoute")
        TimerScreen(
            deviceId = it.deviceId,
            onBackClick = onBackClick
        )
    }
}
