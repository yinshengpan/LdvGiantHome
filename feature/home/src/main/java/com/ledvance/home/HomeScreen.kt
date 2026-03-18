package com.ledvance.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:53
 * Describe : HomeScreen
 */
@Composable
internal fun HomeScreen(
    viewModel: HomeContract = hiltViewModel<HomeViewModel>(),
    onToAddNewDevice: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LedvanceScreen(
        topBarContainerColor = AppTheme.colors.primaryBackground,
        topBarContentColor = AppTheme.colors.primaryContent,
        horizontalAlignment = Alignment.CenterHorizontally,
        actionIconPainter = painterResource(R.drawable.ic_add),
        onActionPressed = onToAddNewDevice,
        verticalArrangement = Arrangement.Center,
        title = "Ldv Giant Home",
    ) {
        when (uiState) {
            HomeContract.UiState.Empty -> {}
            HomeContract.UiState.Loading -> {}
            is HomeContract.UiState.Success -> {
                HomeScreenContent(
                    uiState = uiState as HomeContract.UiState.Success,
                    onSwitchChange = { device, switch -> },
                    onDeviceClick = {

                    },
                )
            }
        }
    }
}