package com.ledvance.energy.manager.navigation

import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.ledvance.energy.manager.extensions.launchCustomChromeTab
import com.ledvance.energy.manager.screen.DeviceDetailScreen
import com.ledvance.energy.manager.screen.DeviceListScreen
import com.ledvance.energy.manager.screen.FirmwareUpdateScreen
import com.ledvance.energy.manager.screen.LanguageScreen
import com.ledvance.energy.manager.screen.LicenseContentScreen
import com.ledvance.energy.manager.screen.LicensesScreen
import com.ledvance.energy.manager.screen.ScanQRCodeScreen
import com.ledvance.energy.manager.screen.SetHistoryScreen
import com.ledvance.energy.manager.state.LedvanceAppState
import com.ledvance.ui.theme.AppTheme
import com.ledvance.utils.DeviceManager
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/19 13:33
 * Describe : MainNavigation
 */
private const val TAG = "MainNavigation"

@Composable
fun MainNavigation(
    appState: LedvanceAppState,
) {
    val context = LocalContext.current
    val chromeTabColor = AppTheme.colors.screenBackground
    val backStack = rememberMutableStateListOf<NavigationRoute>(DeviceListRoute)
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
            entry<DeviceListRoute> {
                PageLifecycleLogger("DeviceListRoute")
                DeviceListScreen(appState, onGotoPage = {
                    backStack.add(it)
                })

            }
            entry<DeviceDetailRoute> {
                PageLifecycleLogger("DeviceDetailRoute")
                DeviceDetailScreen(device = it.device, onBack = {
                    backStack.removeLastOrNull()
                }, onGotoPage = {
                    backStack.add(it)
                })
            }
            entry<LanguageRoute> {
                PageLifecycleLogger("LanguageRoute")
                LanguageScreen(onBack = {
                    backStack.removeLastOrNull()
                })
            }

            entry<FirmwareUpdateRoute> {
                PageLifecycleLogger("FirmwareUpdateRoute")
                FirmwareUpdateScreen(onBack = { isBackToDeviceList ->
                    if (isBackToDeviceList) {
                        backStack.removeIf { it is FirmwareUpdateRoute || it is DeviceDetailRoute }
                    } else {
                        backStack.removeLastOrNull()
                    }
                })
            }

            entry<QRCodeScanRoute> { route ->
                PageLifecycleLogger("QRCodeScanRoute")
                ScanQRCodeScreen(onBack = {
                    backStack.removeLastOrNull()
                }, onResult = {
                    DeviceManager.setSN(route.device.address, it)
                    backStack.removeAll { it is QRCodeScanRoute }
                    backStack.add(DeviceDetailRoute(route.device.copy(sn = it)))
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

            entry<SetHistoryRoute> {
                PageLifecycleLogger("SetHistoryRoute")
                SetHistoryScreen(onBack = {
                    backStack.removeLastOrNull()
                })
            }
        }
    )
}

@Composable
private fun PageLifecycleLogger(pageName: String) {
    DisposableEffect(pageName) {
        Timber.tag(TAG).d("Page shown: $pageName")
        onDispose {
            Timber.tag(TAG).d("Page destroyed: $pageName")
        }
    }
}