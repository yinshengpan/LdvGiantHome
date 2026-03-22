package com.ledvance.light.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.light.LightDetailsScreen
import com.ledvance.light.component.CardFeature
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : LightDetailsNavigation
 */
@Serializable
data class LightDetailsRoute(val deviceId: DeviceId) : NavigationRoute

fun SnapshotStateList<Any>.navigateToLightDetails(deviceId: DeviceId) {
    add(LightDetailsRoute(deviceId))
}

internal fun SnapshotStateList<Any>.navigateToLightFeature(deviceId: DeviceId, feature: CardFeature) {
    when (feature) {
        CardFeature.Scene -> navigateToScenes(deviceId)
        CardFeature.Timer -> navigateToTimer(deviceId)
        CardFeature.Music -> navigateToMusic(deviceId)
        CardFeature.Mode -> navigateToMode(deviceId)
    }
}

fun EntryProviderScope<Any>.lightNavGraph(
    backStack: SnapshotStateList<Any>,
    onNavigateToSetting: (DeviceId) -> Unit,
    onBackClick: () -> Unit,
) {
    lightScreen(
        onNavigateToSetting = onNavigateToSetting,
        onNavigateToFeature = { device, feature ->
            backStack.navigateToLightFeature(device, feature)
        },
        onBackClick = onBackClick,
    )
    scenesScreen(onBackClick = onBackClick)
    timerScreen(onBackClick = onBackClick)
    musicScreen(onBackClick = onBackClick)
    modeScreen(onBackClick = onBackClick)
}

internal fun EntryProviderScope<Any>.lightScreen(
    onNavigateToSetting: (DeviceId) -> Unit,
    onNavigateToFeature: (DeviceId, CardFeature) -> Unit,
    onBackClick: () -> Unit,
) {
    entry<LightDetailsRoute> {
        PageLifecycleLogger("LightDetailsRoute")
        LightDetailsScreen(
            deviceId = it.deviceId,
            onNavigateToSetting = onNavigateToSetting,
            onNavigateToFeature = onNavigateToFeature,
            onBackClick = onBackClick,
        )
    }
}