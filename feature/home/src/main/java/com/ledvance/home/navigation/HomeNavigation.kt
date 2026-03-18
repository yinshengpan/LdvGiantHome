package com.ledvance.home.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.home.HomeScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : HomeNavigation
 */
@Serializable
data object HomeRoute : NavigationRoute

@Serializable
data class ControlPanelRoute(val mac: String) : NavigationRoute

fun SnapshotStateList<Any>.navigateToHome() {
    add(HomeRoute)
}

fun SnapshotStateList<Any>.navigateToControlPanel(mac: String) {
    add(ControlPanelRoute(mac))
}

fun EntryProviderScope<Any>.homeScreen(
    onNavigateToAddNewDevice: () -> Unit,
    onNavigateToControlPanel: (String) -> Unit
) {
    entry<HomeRoute> {
        PageLifecycleLogger("HomeRoute")
        HomeScreen(
            onToAddNewDevice = onNavigateToAddNewDevice,
            onNavigateToControlPanel = onNavigateToControlPanel
        )
    }

    entry<ControlPanelRoute> { route ->
        PageLifecycleLogger("ControlPanelRoute")
        com.ledvance.home.control.ControlPanelScreen(
            onNavigateBack = {
                // How navigation back is handled depends on the app, usually popping state list
            }
        )
    }
}