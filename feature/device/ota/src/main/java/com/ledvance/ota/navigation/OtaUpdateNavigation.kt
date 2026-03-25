package com.ledvance.ota.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.domain.bean.DeviceId
import com.ledvance.ota.OtaUpdateScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : OtaUpdateNavigation
 */
@Serializable
internal data class OtaUpdateRoute(val deviceId: DeviceId) : NavigationRoute

fun SnapshotStateList<Any>.navigateToOtaUpdate(deviceId: DeviceId) {
    add(OtaUpdateRoute(deviceId))
}

fun EntryProviderScope<Any>.otaUpdateScreen(
    onBack: () -> Unit,
) {
    entry<OtaUpdateRoute> {
        PageLifecycleLogger("OtaUpdateRoute")
        OtaUpdateScreen(
            deviceId = it.deviceId,
            onBack = onBack
        )
    }
}