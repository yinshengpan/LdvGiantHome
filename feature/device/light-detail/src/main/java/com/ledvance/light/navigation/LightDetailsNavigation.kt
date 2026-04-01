package com.ledvance.light.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.light.LightDetailsScreen
import com.ledvance.light.component.CardFeature
import com.ledvance.mode.navigation.modeScreen
import com.ledvance.mode.navigation.navigateToMode
import com.ledvance.music.navigation.musicScreen
import com.ledvance.music.navigation.navigateToMusic
import com.ledvance.ota.navigation.navigateToOtaUpdate
import com.ledvance.ota.navigation.otaUpdateScreen
import com.ledvance.scene.navigation.navigateToScenes
import com.ledvance.scene.navigation.scenesScreen
import com.ledvance.setting.navigation.navigateToSetting
import com.ledvance.setting.navigation.settingScreen
import com.ledvance.timer.navigation.navigateToTimer
import com.ledvance.timer.navigation.timerScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : LightDetailsNavigation
 */
@Serializable
internal data class LightDetailsRoute(val deviceId: DeviceId) : NavigationRoute

fun SnapshotStateList<Any>.navigateToLightDetails(deviceId: DeviceId) {
    add(LightDetailsRoute(deviceId))
}

fun SnapshotStateList<Any>.isTopLightDetails(deviceId: DeviceId): Boolean {
    val top = lastOrNull()
    return top is LightDetailsRoute && top.deviceId == deviceId
}

internal fun SnapshotStateList<Any>.navigateToLightFeature(deviceId: DeviceId, feature: CardFeature) {
    when (feature) {
        CardFeature.Scene -> navigateToScenes(deviceId)
        CardFeature.Timer -> navigateToTimer(deviceId)
        CardFeature.Music -> navigateToMusic(deviceId)
        CardFeature.Mode -> navigateToMode(deviceId)
        else -> {}
    }
}

fun EntryProviderScope<Any>.lightNavGraph(
    backStack: SnapshotStateList<Any>,
    onNavigateToHome: () -> Unit,
    onBackClick: () -> Unit,
) {
    lightScreen(
        onNavigateToSetting = {
            backStack.navigateToSetting(it)
        },
        onNavigateToFeature = { device, feature ->
            backStack.navigateToLightFeature(device, feature)
        },
        onBackClick = onBackClick,
    )
    scenesScreen(onBackClick = onBackClick)
    timerScreen(onBackClick = onBackClick)
    musicScreen(onBackClick = onBackClick)
    modeScreen(onBackClick = onBackClick)

    settingScreen(
        onBackClick = {
            backStack.removeLastOrNull()
        },
        onDeleteSuccess = onNavigateToHome,
        onNavigateToOta = {
            backStack.navigateToOtaUpdate(it)
        }
    )

    otaUpdateScreen(onBack = {
        backStack.removeLastOrNull()
    })
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