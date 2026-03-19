package com.ledvance.profile.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.profile.ProfileScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : ProfileNavigation
 */
@Serializable
data object ProfileRoute : NavigationRoute

fun SnapshotStateList<Any>.navigateToProfile() {
    add(ProfileRoute)
}

fun EntryProviderScope<Any>.profileScreen(
    onNavigateToAddNewDevice: () -> Unit,
) {
    entry<ProfileRoute> {
        PageLifecycleLogger("ProfileRoute")
        ProfileScreen(
            onToAddNewDevice = onNavigateToAddNewDevice
        )
    }
}