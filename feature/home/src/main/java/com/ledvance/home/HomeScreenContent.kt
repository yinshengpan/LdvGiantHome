package com.ledvance.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceUiItem
import com.ledvance.domain.bean.asMacAddress
import com.ledvance.ui.R
import com.ledvance.ui.component.LedvanceSwitch
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.extensions.getIconResId
import com.ledvance.ui.theme.AppTheme

@Composable
internal fun HomeScreenContent(
    uiState: HomeContract.UiState.Success,
    onSwitchChange: (DeviceId, Boolean) -> Unit,
    onConnectClick: (DeviceId) -> Unit,
    onDisconnectClick: (DeviceId) -> Unit,
    onDeviceClick: (DeviceId) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        contentPadding = PaddingValues(20.dp)
    ) {
        items(
            items = uiState.devices,
            key = { it.deviceId.asMacAddress() },
            span = { GridItemSpan(1) }) { device ->
            DeviceItem(
                device = device,
                isOnline = uiState.onlineMap[device.deviceId] ?: false,
                switch = device.power,
                onSwitchChange = onSwitchChange,
                onConnectClick = onConnectClick,
                onDisconnectClick = onDisconnectClick,
                onClick = onDeviceClick
            )
        }
    }
}

@Composable
fun DeviceItem(
    device: DeviceUiItem,
    isOnline: Boolean,
    switch: Boolean,
    onSwitchChange: (DeviceId, Boolean) -> Unit,
    onConnectClick: (DeviceId) -> Unit,
    onDisconnectClick: (DeviceId) -> Unit,
    onClick: (DeviceId) -> Unit
) {
    val colorFilter = remember {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0F) })
    }
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.screenBackground),
        shape = RoundedCornerShape(10.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(134.dp)
                .clip(RoundedCornerShape(10.dp))
                .debouncedClickable(onClick = { onClick.invoke(device.deviceId) })
        ) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(device.deviceType.getIconResId()),
                    contentDescription = "",
                    modifier = Modifier
                        .size(60.dp)
                        .clip(shape = RoundedCornerShape(6.dp)),
                    colorFilter = if (!isOnline) colorFilter else null
                )
                Spacer(modifier = Modifier.weight(1f))
                if (!isOnline) {
                    Icon(
                        painter = painterResource(R.drawable.ic_ble_disabled),
                        contentDescription = "offline",
                        modifier = Modifier
                            .padding(top = 10.dp, end = 10.dp)
                            .size(24.dp),
                    )
                } else {
                    LedvanceSwitch(
                        checked = switch,
                        onCheckedChange = { onSwitchChange.invoke(device.deviceId, it) },
                        modifier = Modifier.padding(top = 10.dp, end = 10.dp),
                    )
                }
            }
            Text(
                text = device.name,
                maxLines = 2,
                color = AppTheme.colors.title,
                overflow = TextOverflow.Ellipsis,
                style = AppTheme.typography.bodyMedium,
                modifier = Modifier.padding(top = 20.dp, start = 10.dp, end = 10.dp)
            )
        }
    }
}