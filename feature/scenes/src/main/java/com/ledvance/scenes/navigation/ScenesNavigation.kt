package com.ledvance.scenes.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.scenes.ScenesScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : ScenesNavigation
 */
@Serializable
data object ScenesRoute : NavigationRoute

fun SnapshotStateList<Any>.navigateToScenes() {
    add(ScenesRoute)
}

fun EntryProviderScope<Any>.scenesScreen(
    onNavigateToAddNewDevice: () -> Unit,
) {
    entry<ScenesRoute> {
        PageLifecycleLogger("ScenesRoute")
        ScenesScreen(
            onToAddNewDevice = onNavigateToAddNewDevice
        )
    }
}