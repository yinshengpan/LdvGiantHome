package com.ledvance.home.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.ui.NavDisplay
import com.ledvance.domain.bean.DeviceId
import com.ledvance.home.HomeScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : HomeNavigation
 */
@Serializable
data object HomeRoute : NavigationRoute

fun SnapshotStateList<Any>.navigateToHome() {
    add(HomeRoute)
}

private val noAnimationMetadata =
    NavDisplay.transitionSpec { EnterTransition.None togetherWith ExitTransition.None } +
    NavDisplay.popTransitionSpec { EnterTransition.None togetherWith ExitTransition.None } +
    NavDisplay.predictivePopTransitionSpec { EnterTransition.None togetherWith ExitTransition.None }

fun EntryProviderScope<Any>.homeScreen(
    onNavigateToAddNewDevice: () -> Unit,
    onNavigateToControlPanel: (DeviceId) -> Unit
) {
    entry<HomeRoute>(
        metadata = noAnimationMetadata
    ) {
        PageLifecycleLogger("HomeRoute")
        HomeScreen(
            onToAddNewDevice = onNavigateToAddNewDevice,
            onNavigateToControlPanel = onNavigateToControlPanel
        )
    }
}