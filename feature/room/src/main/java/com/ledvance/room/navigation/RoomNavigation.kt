package com.ledvance.room.navigation

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation3.runtime.EntryProviderScope
import com.ledvance.room.RoomScreen
import com.ledvance.ui.navigation.NavigationRoute
import com.ledvance.ui.navigation.PageLifecycleLogger
import kotlinx.serialization.Serializable

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:37
 * Describe : RoomNavigation
 */
@Serializable
data object RoomRoute : NavigationRoute

@Serializable
data object DevicesRoute : NavigationRoute

fun SnapshotStateList<Any>.navigateToRoom() {
    add(RoomRoute)
}

fun EntryProviderScope<Any>.roomScreen() {
    entry<RoomRoute> {
        PageLifecycleLogger("RoomRoute")
        RoomScreen()
    }
    entry<DevicesRoute> {
        PageLifecycleLogger("RoomRoute")
        RoomScreen()
    }
}