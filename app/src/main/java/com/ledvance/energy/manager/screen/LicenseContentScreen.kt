package com.ledvance.energy.manager.screen

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.energy.manager.model.License
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/20/25 10:19
 * Describe : LicenseContentScreen
 */
@Composable
fun LicenseContentScreen(license: License, onBack: () -> Unit) {
    LedvanceScreen(title = license.libName, onBackPressed = onBack) {
        SelectionContainer(modifier = Modifier.fillMaxSize()) {
            Text(
                text = license.content, style = AppTheme.typography.bodyLarge.copy(fontSize = 14.sp),
                color = AppTheme.colors.body,
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp, vertical = 20.dp),
            )
        }
    }
}