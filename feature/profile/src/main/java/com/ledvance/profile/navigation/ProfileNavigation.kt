package com.ledvance.profile.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay
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

private val noAnimationMetadata =
    NavDisplay.transitionSpec { EnterTransition.None togetherWith ExitTransition.None } +
    NavDisplay.popTransitionSpec { EnterTransition.None togetherWith ExitTransition.None } +
    NavDisplay.predictivePopTransitionSpec { EnterTransition.None togetherWith ExitTransition.None }

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
    entry<ProfileRoute>(
        metadata = noAnimationMetadata
    ) {
        PageLifecycleLogger("ProfileRoute")
        ProfileScreen(
            onNavigateToLicenses = onNavigateToLicenses
        )
    }
}