package com.ledvance.scene.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.scene.ScenesScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : ScenesNavigation
 */
@Serializable
internal data class ScenesRoute(val deviceId: DeviceId) : NavigationRoute

fun SnapshotStateList<Any>.navigateToScenes(deviceId: DeviceId) {
    add(ScenesRoute(deviceId))
}

fun EntryProviderScope<Any>.scenesScreen(
    onBackClick: () -> Unit
) {
    entry<ScenesRoute> {
        PageLifecycleLogger("ScenesRoute")
        ScenesScreen(
            deviceId = it.deviceId,
            onBackClick = onBackClick
        )
    }
}
