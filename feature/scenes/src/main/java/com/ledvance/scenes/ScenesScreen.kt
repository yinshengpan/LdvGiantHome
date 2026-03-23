package com.ledvance.scenes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.painterResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:53
 * Describe : ScenesScreen
 */
@Composable
internal fun ScenesScreen(
    viewModel: ScenesContract = hiltViewModel<ScenesViewModel>(),
    onToAddNewDevice: () -> Unit,
) {
    LedvanceScreen(
        topBarContainerColor = AppTheme.colors.primaryBackground,
        topBarContentColor = AppTheme.colors.primaryContent,
        horizontalAlignment = Alignment.CenterHorizontally,
        actionIconPainter = painterResource(R.drawable.ic_add),
        onActionPressed = onToAddNewDevice,
        verticalArrangement = Arrangement.Center,
        title = "Ldv Home",
    ) {

    }
}