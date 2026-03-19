package com.ledvance.light.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.light.LightDetailsScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : LightDetailsNavigation
 */
@Serializable
data class LightDetailsRoute(val address: String) : NavigationRoute

fun SnapshotStateList<Any>.navigateToLightDetails(address: String) {
    add(LightDetailsRoute(address))
}

fun EntryProviderScope<Any>.lightDetailsScreen(
    onNavigateToAddNewDevice: () -> Unit,
) {
    entry<LightDetailsRoute> {
        PageLifecycleLogger("LightDetailsRoute")
        LightDetailsScreen(
            onToAddNewDevice = onNavigateToAddNewDevice
        )
    }
}