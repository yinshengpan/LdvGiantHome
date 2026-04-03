package com.ledvance.meluce.navigation

import com.ledvance.home.navigation.HomeRoute
import com.ledvance.profile.navigation.ProfileRoute
import com.ledvance.room.navigation.DevicesRoute
import com.ledvance.room.navigation.RoomRoute
import com.ledvance.ui.R
import com.ledvance.ui.navigation.NavigationRoute

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/23 10:48
 * Describe : TopLevelDestination
 */
enum class TopLevelDestination(
    val selectedIconId: Int,
    val unselectedIconId: Int,
    val iconTextId: Int,
    val route: NavigationRoute,
) {
    HOME(
        selectedIconId = R.mipmap.main_tab_s_home,
        unselectedIconId = R.mipmap.main_tab_n_home,
        iconTextId = R.string.tab_home,
        route = HomeRoute
    ),

    DEVICES(
        selectedIconId = R.mipmap.main_tab_s_devices,
        unselectedIconId = R.mipmap.main_tab_n_devices,
        iconTextId = R.string.tab_devices,
        route = DevicesRoute
    ),

    SCENES(
        selectedIconId = R.mipmap.main_tab_s_scenes,
        unselectedIconId = R.mipmap.main_tab_n_scenes,
        iconTextId = R.string.tab_scenes,
        route = RoomRoute
    ),

    PROFILE(
        selectedIconId = R.mipmap.main_tab_s_settings,
        unselectedIconId = R.mipmap.main_tab_n_settings,
        iconTextId = R.string.tab_settings,
        route = ProfileRoute
    )
}