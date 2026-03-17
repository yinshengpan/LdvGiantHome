package com.ledvance.energy.manager.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ledvance.database.model.SetTripCurrentHistoryEntity
import com.ledvance.database.model.SetTripCurrentType
import com.ledvance.energy.manager.viewmodel.SetHistoryViewModel
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/21/25 17:49
 * Describe : SetHistoryScreen
 */
@Composable
fun SetHistoryScreen(viewModel: SetHistoryViewModel = hiltViewModel(), onBack: () -> Unit) {
    val historyList by viewModel.historyListFlow.collectAsStateWithLifecycle()
    LedvanceScreen(title = "Set History", onBackPressed = onBack) {
        LazyColumn(modifier = Modifier.fillMaxSize().padding(top = 15.dp)) {
            items(historyList) {
                SetHistoryItem(it)
            }
        }
    }
}

@Composable
private fun SetHistoryItem(history: SetTripCurrentHistoryEntity) {
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
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 16.dp)
            ) {
                Text(
                    text = history.sn,
                    modifier = Modifier
                        .fillMaxWidth(),
                    style = AppTheme.typography.titleMedium.copy(fontSize = 15.sp),
                    color = AppTheme.colors.title
                )

                Text(
                    text = "Trip Current : ${history.tripCurrent}A",
                    style = AppTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = AppTheme.colors.body,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
                Text(
                    text = "Set Time : ${history.createTimeStr}",
                    style = AppTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = AppTheme.colors.body,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp)
                )
            }
            Image(
                painter = painterResource(if (history.type == SetTripCurrentType.Nfc) R.drawable.ic_nfc else R.drawable.bluetooth),
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 5.dp)
                    .size(28.dp)
            )
        }
    }
}