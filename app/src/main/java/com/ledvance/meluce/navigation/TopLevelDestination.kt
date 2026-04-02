package com.ledvance.meluce.navigation

import com.ledvance.home.navigation.HomeRoute
import com.ledvance.profile.navigation.ProfileRoute
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
    val route: NavigationRoute
) {
    HOME(
        selectedIconId = R.drawable.ic_home_fill,
        unselectedIconId = R.drawable.ic_home,
        iconTextId = R.string.tab_home,
        route = HomeRoute
    ),

    PROFILE(
        selectedIconId = R.drawable.ic_my_fill,
        unselectedIconId = R.drawable.ic_my,
        iconTextId = R.string.tab_profile,
        route = ProfileRoute
    )
}