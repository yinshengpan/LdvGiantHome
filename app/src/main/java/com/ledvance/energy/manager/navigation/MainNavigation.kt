package com.ledvance.energy.manager.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ledvance.energy.manager.extensions.launchCustomChromeTab
import com.ledvance.energy.manager.screen.DeviceListScreen
import com.ledvance.energy.manager.screen.LanguageScreen
import com.ledvance.energy.manager.screen.LicenseContentScreen
import com.ledvance.energy.manager.screen.LicensesScreen
import com.ledvance.energy.manager.state.LedvanceAppState
import com.ledvance.home.navigation.HomeRoute
import com.ledvance.home.navigation.homeScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import com.ledvance.ui.theme.AppTheme

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
    val backStack = rememberMutableStateListOf<NavigationRoute>(HomeRoute)
    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
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
            homeScreen(onNavigateToAddNewDevice = {

            })

            entry<LanguageRoute> {
                PageLifecycleLogger("LanguageRoute")
                LanguageScreen(onBack = {
                    backStack.removeLastOrNull()
                })
            }

            entry<OpenSourceLicensesRoute> {
                PageLifecycleLogger("OpenSourceLicensesRoute")
                LicensesScreen(
                    onBack = {
                        backStack.removeLastOrNull()
                    },
                    onClickLicense = {
                        when {
                            it.content.isNotEmpty() -> {
                                backStack.add(LicenseContentRoute(it))
                            }

                            else -> {
                                launchCustomChromeTab(
                                    context = context,
                                    uri = it.url.toUri(),
                                    toolbarColor = chromeTabColor.toArgb()
                                )
                            }
                        }
                    },
                )
            }

            entry<LicenseContentRoute> {
                PageLifecycleLogger("LicenseContentRoute")
                LicenseContentScreen(license = it.license) {
                    backStack.removeLastOrNull()
                }
            }
        }
    )
}

