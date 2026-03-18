package com.ledvance.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.component.LottieAsset
import com.ledvance.ui.state.rememberBluetoothBusinessState
import com.ledvance.ui.theme.AppTheme
import com.ledvance.utils.BluetoothManager
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:53
 * Describe : HomeScreen
 */
@Composable
internal fun SearchScreen(
    viewModel: SearchContract = hiltViewModel<SearchViewModel>(),
    onBackClick: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bluetoothEnableState by BluetoothManager.bluetoothEnableState.collectAsStateWithLifecycle()
    var bluetoothPermission by remember { mutableStateOf(false) }
    val bluetoothBusinessState by rememberBluetoothBusinessState()

    LifecycleResumeEffect(Unit) {
        bluetoothPermission = bluetoothBusinessState.hasAllow()
        onPauseOrDispose { }
    }

    DisposableEffect(key1 = bluetoothPermission, key2 = bluetoothEnableState) {
        if (bluetoothEnableState && bluetoothPermission) {
            viewModel.startBleScan()
        } else {
            viewModel.stopBleScan()
        }

        onDispose {
            viewModel.stopBleScan()
        }
    }

    LedvanceScreen(
        topBarContainerColor = AppTheme.colors.primaryBackground,
        topBarContentColor = AppTheme.colors.primaryContent,
        horizontalAlignment = Alignment.CenterHorizontally,
        onBackPressed = onBackClick,
        verticalArrangement = Arrangement.Center,
        title = "Search For Devices",
    ) {
        when (uiState) {
            is SearchContract.UiState.Success -> {
                SearchScreenContent(uiState = uiState as SearchContract.UiState.Success)
            }

            else -> {
                LottieAsset(
                    assetName = "ble.lottie",
                    modifier = Modifier
                        .padding(top = 76.dp)
                        .align(Alignment.TopCenter)
                )
            }
        }
    }
}