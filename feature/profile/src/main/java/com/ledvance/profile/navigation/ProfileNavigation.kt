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

fun EntryProviderScope<Any>.profileNavGraph(
    backStack: SnapshotStateList<Any>,
    onLaunchCustomChromeTab: (android.net.Uri) -> Unit
) {
    profileScreen(
        onNavigateToLicenses = {
            backStack.navigateToLicenses()
        }
    )

    licensesScreen(
        onBack = {
            backStack.removeLastOrNull()
        },
        onLaunchCustomChromeTab = onLaunchCustomChromeTab,
        onNavigateToLicenseContent = {
            backStack.navigateToLicenseContent(it)
        },
    )

    licenseContentScreen(onBack = {
        backStack.removeLastOrNull()
    })
}

internal fun EntryProviderScope<Any>.profileScreen(
    onNavigateToLicenses: () -> Unit,
) {
    entry<ProfileRoute> {
        PageLifecycleLogger("ProfileRoute")
        ProfileScreen(
            onNavigateToLicenses = onNavigateToLicenses
        )
    }
}