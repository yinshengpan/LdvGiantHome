package com.ledvance.energy.manager.state

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ledvance.energy.manager.navigation.MainNavigation
import com.ledvance.energy.manager.navigation.MainNavigationScaffold
import com.ledvance.energy.manager.navigation.TopLevelDestination
import com.ledvance.home.navigation.HomeRoute
import com.ledvance.profile.navigation.ProfileRoute
import com.ledvance.ui.component.showToast
import com.ledvance.ui.navigation.NavigationRoute
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
    
    val currentRoute = appState.backStack.lastOrNull() as? NavigationRoute
    val showBottomBar = currentRoute == HomeRoute || currentRoute == ProfileRoute
    
    MainNavigationScaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (showBottomBar) {
                LedvanceBottomBar(
                    destinations = TopLevelDestination.entries,
                    onNavigateToDestination = { dest ->
                        if (currentRoute != dest.route) {
                            appState.backStack.clear()
                            appState.backStack.add(dest.route)
                        }
                    },
                    currentRoute = currentRoute
                )
            }
        }
    ) {
        MainNavigation(appState = appState)
    }
}

@Composable
private fun LedvanceBottomBar(
    destinations: List<TopLevelDestination>,
    onNavigateToDestination: (TopLevelDestination) -> Unit,
    currentRoute: NavigationRoute?,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        HorizontalDivider(
            thickness = 0.5.dp,
            color = AppTheme.colors.title.copy(alpha = 0.1f)
        )
        NavigationBar(
            modifier = Modifier.height(68.dp),
            containerColor = AppTheme.colors.screenBackground,
            contentColor = AppTheme.colors.title,
            tonalElevation = 0.dp
        ) {
            destinations.forEach { destination ->
                val selected = currentRoute == destination.route
                NavigationBarItem(
                    selected = selected,
                    onClick = {
                        onNavigateToDestination(destination)
                    },
                    icon = {
                        val iconId = if (selected) destination.selectedIconId else destination.unselectedIconId
                        Icon(
                            painter = painterResource(id = iconId),
                            contentDescription = stringResource(id = destination.iconTextId),
                            tint = AppTheme.colors.primary,
                            modifier = Modifier.size(28.dp)
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}