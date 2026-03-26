package com.ledvance.energy.manager.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ledvance.energy.manager.state.LedvanceAppState
import com.ledvance.home.navigation.HomeRoute
import com.ledvance.home.navigation.homeScreen
import com.ledvance.light.navigation.lightNavGraph
import com.ledvance.light.navigation.navigateToLightDetails
import com.ledvance.profile.navigation.profileNavGraph
import com.ledvance.search.navigation.navigateToSearch
import com.ledvance.search.navigation.searchScreen
import com.ledvance.ui.theme.AppTheme
import com.ledvance.utils.extensions.launchCustomChromeTab

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/19 13:33
 * Describe : MainNavigation
 */
@Composable
fun MainNavigation(appState: LedvanceAppState) {
    val context = LocalContext.current
    val chromeTabColor = AppTheme.colors.screenBackground
    val backStack = appState.backStack
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.back() },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
        ),
        transitionSpec = {
            // Slide in from right when navigating forward
            slideInHorizontally(initialOffsetX = { it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { -it })
        },
        popTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        predictivePopTransitionSpec = {
            // Slide in from left when navigating back
            slideInHorizontally(initialOffsetX = { -it }) togetherWith
                    slideOutHorizontally(targetOffsetX = { it })
        },
        entryProvider = entryProvider {
            homeScreen(
                onNavigateToAddNewDevice = {
                    backStack.navigateToSearch()
                },
                onNavigateToControlPanel = {
                    backStack.navigateToLightDetails(it)
                }
            )

            profileNavGraph(
                backStack = backStack,
                onLaunchCustomChromeTab = {
                    launchCustomChromeTab(
                        context = context,
                        uri = it,
                        toolbarColor = chromeTabColor.toArgb()
                    )
                }
            )

            searchScreen(onBackClick = {
                backStack.back()
            })

            lightNavGraph(
                backStack = backStack,
                onNavigateToHome = {
                    backStack.clear()
                    backStack.add(HomeRoute)
                },
                onBackClick = {
                    backStack.back()
                },
            )
        }
    )
}


fun SnapshotStateList<Any>.back() {
    removeLastOrNull()
}
