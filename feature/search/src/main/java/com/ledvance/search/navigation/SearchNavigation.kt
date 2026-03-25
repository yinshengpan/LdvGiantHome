package com.ledvance.search.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.search.SearchScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : SearchNavigation
 */
@Serializable
internal data object SearchRoute : NavigationRoute

fun SnapshotStateList<Any>.navigateToSearch() {
    add(SearchRoute)
}

fun EntryProviderScope<Any>.searchScreen(
    onBackClick: () -> Unit,
) {
    entry<SearchRoute> {
        PageLifecycleLogger("SearchRoute")
        SearchScreen(
            onBackClick = onBackClick,
        )
    }
}