package com.ledvance.meluce.state

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.domain.bean.DeviceId
import com.ledvance.home.navigation.HomeRoute
import com.ledvance.light.navigation.isTopLightDetails
import com.ledvance.light.navigation.navigateToLightDetails
import com.ledvance.meluce.navigation.MainNavigation
import com.ledvance.meluce.navigation.MainNavigationScaffold
import com.ledvance.meluce.navigation.TopLevelDestination
import com.ledvance.meluce.viewmodel.MainViewModel
import com.ledvance.profile.navigation.ProfileRoute
import com.ledvance.room.navigation.DevicesRoute
import com.ledvance.room.navigation.RoomRoute
import com.ledvance.ui.CardView
import com.ledvance.ui.component.SnackbarManager
import com.ledvance.ui.component.SnackbarMessage
import com.ledvance.ui.component.showToast
import com.ledvance.ui.extensions.debouncedClickable
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
    mainViewModel: MainViewModel = hiltViewModel(),
    appState: LedvanceAppState = rememberLedvanceAppState(),
) {
    val nfcModel by mainViewModel.nfcModel.collectAsStateWithLifecycle()

    LaunchedEffect(nfcModel) {
        val nfcInfo = nfcModel?.nfcInfo
        if (nfcInfo != null) {
            val deviceId = DeviceId(macAddress = nfcInfo.macAddress, deviceType = nfcInfo.deviceType)
            mainViewModel.connectDevice(deviceId)
            mainViewModel.resetNfc()
            if (!appState.backStack.isTopLightDetails(deviceId)) {
                if (appState.backStack.lastOrNull() != HomeRoute) {
                    appState.backStack.clear()
                    appState.backStack.add(HomeRoute)
                }
                appState.backStack.navigateToLightDetails(deviceId)
            }
        }
    }

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
            || currentRoute == RoomRoute || currentRoute == DevicesRoute

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
    CardView(
        paddingValues = PaddingValues(start = 15.dp, end = 15.dp, bottom = 15.dp),
        modifier = Modifier
            .then(modifier)
            .navigationBarsPadding()
    ) {
        NavigationBar(
            modifier = Modifier
                .height(80.dp)
                .padding(horizontal = 10.dp),
            containerColor = AppTheme.colors.cardBackground,
            contentColor = Color.Unspecified,
            tonalElevation = 0.dp
        ) {
            destinations.forEach { destination ->
                val selected = currentRoute == destination.route
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .debouncedClickable(indication = null, onClick = { onNavigateToDestination(destination) }),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    val iconId = if (selected) destination.selectedIconId else destination.unselectedIconId
                    Icon(
                        painter = painterResource(id = iconId),
                        contentDescription = stringResource(id = destination.iconTextId),
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = stringResource(id = destination.iconTextId),
                        style = AppTheme.typography.bodyMedium.copy(fontSize = 11.sp, fontWeight = FontWeight.W400),
                        modifier = Modifier.padding(top = 5.dp),
                        color = if (selected) AppTheme.colors.mainTabSelected else AppTheme.colors.mainTabUnselected,
                    )
                }
            }
        }
    }
}