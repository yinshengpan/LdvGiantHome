package com.ledvance.setting.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.setting.SettingScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : SettingNavigation
 */
@Serializable
internal data class SettingRoute(val deviceId: DeviceId) : NavigationRoute

fun SnapshotStateList<Any>.navigateToSetting(deviceId: DeviceId) {
    add(SettingRoute(deviceId))
}

fun EntryProviderScope<Any>.settingScreen(
    onBackClick: () -> Unit,
    onDeleteSuccess: () -> Unit,
    onNavigateToOta: (DeviceId) -> Unit,
) {
    entry<SettingRoute> {
        PageLifecycleLogger("SettingRoute")
        SettingScreen(
            deviceId = it.deviceId,
            onBackClick = onBackClick,
            onDeleteSuccess = onDeleteSuccess,
            onNavigateToOta = { onNavigateToOta.invoke(it.deviceId) },
        )
    }
}