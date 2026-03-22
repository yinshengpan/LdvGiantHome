package com.ledvance.profile.screen.licenses

import com.ledvance.domain.bean.License

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : LicensesScreen
 */
@Composable
internal fun LicensesScreen(
    viewModel: LicensesContract = hiltViewModel<LicensesViewModel>(),
    onBack: () -> Unit,
    onClickLicense: (License) -> Unit
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    LedvanceScreen(title = stringResource(R.string.open_source_licenses), onBackPressed = onBack) {
        when (uiState) {
            LicensesContract.UiState.Error -> {}
            LicensesContract.UiState.Loading -> {}
            is LicensesContract.UiState.Success -> {
                LicensesScreenContent(
                    uiState = uiState as LicensesContract.UiState.Success,
                    onClickLicense = onClickLicense
                )
            }
        }
    }
}
