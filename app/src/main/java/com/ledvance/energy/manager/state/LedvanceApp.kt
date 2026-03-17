package com.ledvance.energy.manager.state

import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import com.ledvance.energy.manager.navigation.MainNavigation
import com.ledvance.energy.manager.navigation.MainNavigationScaffold
import com.ledvance.energy.manager.navigation.TopLevelDestination
import com.ledvance.ui.theme.AppTheme
import com.ledvance.ui.theme.LocalSnackBarHostState

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/22 16:04
 * Describe : LedvanceApp
 */
@Composable
fun LedvanceApp(
    appState: LedvanceAppState = rememberLedvanceAppState(),
) {
    MainNavigationScaffold(
        snackbarHost = { SnackbarHost(LocalSnackBarHostState.current) }
    ) {
        MainNavigation(appState = appState,)
    }
}

@Composable
private fun LedvanceBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentDestination: NavDestination?,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        contentColor = AppTheme.colors.title,
        tonalElevation = 0.dp
    ) {
        destinations.forEach { destination ->
            val selected = currentDestination.isTopLevelDestinationInHierarchy(destination)
            NavigationBarItem(
                modifier = modifier,
                selected = selected,
                onClick = {
                    onNavigateToDestination(destination)
                },
                icon = {
                    Icon(
                        painter = painterResource(id = destination.iconId),
                        contentDescription = stringResource(id = destination.iconTextId)
                    )
                },
            )
        }
    }
}

private fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
    this?.hierarchy?.any {
        it.route?.contains(destination.name, true) ?: false
    } ?: false