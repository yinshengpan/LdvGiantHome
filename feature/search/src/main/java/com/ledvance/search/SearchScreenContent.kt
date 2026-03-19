package com.ledvance.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ledvance.ble.bean.ScannedDevice
import com.ledvance.ui.R
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.extensions.getIconResId
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 14:23
 * Describe : SearchScreenContent
 */
@Composable
internal fun SearchScreenContent(
    uiState: SearchContract.UiState.Success,
    onItemClick: (ScannedDevice) -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp)
    ) {
        items(uiState.devices) {
            DeviceItem(device = it, onItemClick = onItemClick)
        }
    }
}

@Composable
private fun DeviceItem(device: ScannedDevice, onItemClick: (ScannedDevice) -> Unit) {
    Card(
        modifier = Modifier
            .padding(24.dp, 6.dp)
            .fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(8.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.screenBackground),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp)
                .debouncedClickable(onClick = {
                    onItemClick.invoke(device)
                }),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Spacer(modifier = Modifier.width(12.dp))
            Image(
                painter = painterResource(device.deviceType.getIconResId()),
                contentDescription = "device image",
                modifier = Modifier.size(32.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
                painter = painterResource(id = R.drawable.ic_ble),
                contentDescription = "ble",
                modifier = Modifier.size(16.dp),
                tint = AppTheme.colors.title,
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = device.name,
                modifier = Modifier
                    .weight(1f),
                color = AppTheme.colors.title,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = AppTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
    }
}