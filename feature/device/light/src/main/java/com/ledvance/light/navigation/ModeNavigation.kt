package com.ledvance.light.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.light.screen.mode.ModeScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : ModeNavigation
 */
@Serializable
internal data class ModeRoute(val deviceId: DeviceId) : NavigationRoute

internal fun SnapshotStateList<Any>.navigateToMode(deviceId: DeviceId) {
    add(ModeRoute(deviceId))
}

internal fun EntryProviderScope<Any>.modeScreen(
    onBackClick: () -> Unit
) {
    entry<ModeRoute> {
        PageLifecycleLogger("ModeRoute")
        ModeScreen(
            deviceId = it.deviceId,
            onBackClick = onBackClick
        )
    }
}
