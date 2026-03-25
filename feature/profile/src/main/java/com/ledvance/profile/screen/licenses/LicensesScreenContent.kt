package com.ledvance.profile.screen.licenses

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.domain.bean.License
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Describe : LicensesScreenContent
 */
@Composable
internal fun LicensesScreenContent(
    uiState: LicensesContract.UiState.Success,
    onClickLicense: (License) -> Unit
) {
    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        item { Spacer(modifier = Modifier.height(20.dp)) }
        items(uiState.licenses, key = { it.libUniqueId }) {
            LicenseItem(it, onClickLicense)
        }
        item { Spacer(modifier = Modifier.height(40.dp)) }
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
