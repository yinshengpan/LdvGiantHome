package com.ledvance.setting.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
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
data object SettingRoute : NavigationRoute

fun SnapshotStateList<Any>.navigateToSetting() {
    add(SettingRoute)
}

fun EntryProviderScope<Any>.settingScreen(
    onNavigateToAddNewDevice: () -> Unit,
) {
    entry<SettingRoute> {
        PageLifecycleLogger("SettingRoute")
        SettingScreen(
            onToAddNewDevice = onNavigateToAddNewDevice
        )
    }
}