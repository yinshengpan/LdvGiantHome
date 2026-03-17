package com.ledvance.energy.manager.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.energy.manager.model.License
import com.ledvance.energy.manager.viewmodel.LicensesViewModel
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/19/25 17:17
 * Describe : LicensesScreen
 */
@Composable
fun LicensesScreen(
    viewModel: LicensesViewModel = hiltViewModel(),
    onBack: () -> Unit,
    onClickLicense: (License) -> Unit
) {
    val licenses by viewModel.licensesFlow.collectAsStateWithLifecycle()
    LedvanceScreen(title = stringResource(R.string.open_source_licenses), onBackPressed = onBack) {
        LazyColumn(modifier = Modifier.fillMaxWidth().padding(top = 15.dp)) {
            items(licenses, key = { it.libUniqueId }) {
                LicenseItem(it, onClickLicense)
            }
        }
    }
}

@Composable
private fun LicenseItem(license: License, onItemClick: (License) -> Unit) {
    Card(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 7.5.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White, contentColor = Color.White
        ),
        shape = RoundedCornerShape(7.dp),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(80.dp)
                .debouncedClickable {
                    onItemClick.invoke(license)
                },
            contentAlignment = Alignment.CenterStart
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 15.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = license.libName,
                    style = AppTheme.typography.titleMedium.copy(fontSize = 14.sp),
                    color = AppTheme.colors.title,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = license.name,
                    style = AppTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = AppTheme.colors.body,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 3.dp)
                )
            }
        }
    }
}