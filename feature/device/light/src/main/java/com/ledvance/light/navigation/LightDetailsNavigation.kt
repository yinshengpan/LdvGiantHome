package com.ledvance.light.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.domain.bean.DeviceId
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
data class LightDetailsRoute(val deviceId: DeviceId) : NavigationRoute

fun SnapshotStateList<Any>.navigateToLightDetails(deviceId: DeviceId) {
    add(LightDetailsRoute(deviceId))
}

fun EntryProviderScope<Any>.lightDetailsScreen(
    onNavigateToSetting: (DeviceId) -> Unit,
    onBackClick: () -> Unit,
) {
    entry<LightDetailsRoute> {
        PageLifecycleLogger("LightDetailsRoute")
        LightDetailsScreen(
            deviceId = it.deviceId,
            onNavigateToSetting = onNavigateToSetting,
            onBackClick = onBackClick,
        )
    }
}