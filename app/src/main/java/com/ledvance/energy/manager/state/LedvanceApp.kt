package com.ledvance.energy.manager.state

import android.annotation.SuppressLint
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.ledvance.ui.component.showToast
import com.ledvance.ui.theme.AppTheme
import com.ledvance.ui.theme.LocalSnackBarHostState
import kotlinx.coroutines.flow.collectLatest

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/22 16:04
 * Describe : LedvanceApp
 */
@SuppressLint("LocalContextGetResourceValueCall")
@Composable
fun LedvanceApp(
    appState: LedvanceAppState = rememberLedvanceAppState(),
) {
    val snackbarHostState = LocalSnackBarHostState.current
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        SnackbarManager.messages.collectLatest { message ->
            val text = when (message) {
                is SnackbarMessage.Resource -> context.getString(message.resId)
                is SnackbarMessage.Text -> message.value
            }
            snackbarHostState.showToast(text)
        }
    }
    MainNavigationScaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) {
        MainNavigation(appState = appState)
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