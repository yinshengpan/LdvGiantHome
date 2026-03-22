package com.ledvance.profile.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.net.toUri
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.domain.bean.License
import com.ledvance.profile.screen.licenses.LicenseContentScreen
import com.ledvance.profile.screen.licenses.LicensesScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/3/22 19:40
 * Describe : LicensesNavigation
 */
@Serializable
internal data object LicensesRoute : NavigationRoute

@Serializable
internal data class LicenseContentRoute(val license: License) : NavigationRoute

internal fun SnapshotStateList<Any>.navigateToLicenses() {
    add(LicensesRoute)
}

internal fun SnapshotStateList<Any>.navigateToLicenseContent(license: License) {
    add(LicenseContentRoute(license))
}

internal fun EntryProviderScope<Any>.licensesScreen(
    onBack: () -> Unit,
    onLaunchCustomChromeTab: (uri: android.net.Uri) -> Unit,
    onNavigateToLicenseContent: (license: License) -> Unit,
) {
    entry<LicensesRoute> {
        PageLifecycleLogger("LicensesRoute")
        LicensesScreen(
            onBack = onBack,
            onClickLicense = {
                if (it.content.isNotEmpty()) {
                    onNavigateToLicenseContent.invoke(it)
                } else {
                    onLaunchCustomChromeTab(it.url.toUri())
                }
            }
        )
    }
}

internal fun EntryProviderScope<Any>.licenseContentScreen(
    onBack: () -> Unit,
) {
    entry<LicenseContentRoute> {
        PageLifecycleLogger("LicenseContentRoute")
        LicenseContentScreen(license = it.license, onBack = onBack)
    }
}
