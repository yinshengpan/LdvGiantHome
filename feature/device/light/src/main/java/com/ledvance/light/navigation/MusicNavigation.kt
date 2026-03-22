package com.ledvance.light.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.light.screen.music.MusicScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : MusicNavigation
 */
@Serializable
internal data class MusicRoute(val deviceId: DeviceId) : NavigationRoute

internal fun SnapshotStateList<Any>.navigateToMusic(deviceId: DeviceId) {
    add(MusicRoute(deviceId))
}

internal fun EntryProviderScope<Any>.musicScreen(
    onBackClick: () -> Unit
) {
    entry<MusicRoute> {
        PageLifecycleLogger("MusicRoute")
        MusicScreen(
            deviceId = it.deviceId,
            onBackClick = onBackClick
        )
    }
}
