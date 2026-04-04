package com.ledvance.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.lazy.grid.LazyGridScope
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.domain.bean.DeviceId
import com.ledvance.domain.bean.DeviceUiItem
import com.ledvance.domain.bean.isLdvBedside
import com.ledvance.ui.R
import com.ledvance.ui.component.MeluceSwitch
import com.ledvance.ui.extensions.clipWithBorder
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
    onDeleteClick: (DeviceId) -> Unit,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize().padding(bottom = 80.dp),
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        verticalArrangement = Arrangement.spacedBy(15.dp),
        contentPadding = PaddingValues(22.dp)
    ) {

        homeTopBanner(onlineDeviceCount = uiState.onlineDeviceCount)
        homeDeviceList(
            devices = uiState.devices,
            onSwitchChange = onSwitchChange,
            onConnectClick = onConnectClick,
            onDisconnectClick = onDisconnectClick,
            onDeviceClick = onDeviceClick,
            onDeleteClick = onDeleteClick
        )
        homeScenes()
        homeItemSpacer(height = 30.dp)
    }
}

private fun LazyGridScope.homeDeviceList(
    devices: List<DeviceUiItem>,
    onSwitchChange: (DeviceId, Boolean) -> Unit,
    onConnectClick: (DeviceId) -> Unit,
    onDisconnectClick: (DeviceId) -> Unit,
    onDeviceClick: (DeviceId) -> Unit,
    onDeleteClick: (DeviceId) -> Unit,
) {
    homeItemTitle("Daily Devices")
    items(items = devices, key = { it.deviceId.macAddress }, span = { GridItemSpan(1) }) { device ->
        DeviceItem(
            device = device,
            isOnline = device.isOnline,
            switch = device.power,
            showDeleteIcon = false,
            onSwitchChange = onSwitchChange,
            onConnectClick = onConnectClick,
            onDisconnectClick = onDisconnectClick,
            onClick = onDeviceClick,
            onDeleteClick = onDeleteClick
        )
    }
}

private fun LazyGridScope.homeScenes(modifier: Modifier = Modifier) {
    homeItemTitle("Active Scenes")
    item(span = { GridItemSpan(2) }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(modifier)
                .height(236.3.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Image(
                painter = painterResource(R.mipmap.icon_home_scenes_1),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier.size(width = 205.5.dp, height = 236.3.dp).clip(shape = RoundedCornerShape(12.dp))
            )
            Spacer(modifier = Modifier.weight(1f))
            Column {
                Image(
                    painter = painterResource(R.mipmap.icon_home_scenes_2),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.size(width = 95.4.dp, height = 110.8.dp).clip(shape = RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.weight(1f))
                Image(
                    painter = painterResource(R.mipmap.icon_home_scenes_3),
                    contentDescription = null,
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier.size(width = 95.4.dp, height = 110.8.dp).clip(shape = RoundedCornerShape(12.dp))
                )
            }
        }
    }
}


private fun LazyGridScope.homeItemTitle(title: String, modifier: Modifier = Modifier) {
    item(span = { GridItemSpan(2) }) {
        Text(
            text = title,
            style = AppTheme.typography.bodyMedium.copy(fontSize = 20.sp, fontWeight = FontWeight.W400),
            color = AppTheme.colors.title,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .then(modifier)
        )
    }
}

private fun LazyGridScope.homeItemSpacer(height: Dp = 20.dp) {
    item(span = { GridItemSpan(2) }) {
        Spacer(modifier = Modifier.height(height))
    }
}

private fun LazyGridScope.homeTopBanner(onlineDeviceCount: Int) {
    item(span = { GridItemSpan(2) }) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp)
        ) {
            Image(
                painter = painterResource(R.mipmap.bg_home_banner),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth,
            )
            Row(
                modifier = Modifier
                    .padding(vertical = 20.dp, horizontal = 25.dp)
                    .align(Alignment.BottomCenter),
                verticalAlignment = Alignment.Bottom
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "LIVING ROOM",
                        style = AppTheme.typography.bodyMedium.copy(
                            fontSize = 14.sp,
                            fontWeight = FontWeight.W400
                        ),
                        color = Color.White,
                    )

                    Text(
                        text = "22.5°C",
                        style = AppTheme.typography.bodyMedium.copy(
                            fontSize = 30.sp,
                            fontWeight = FontWeight.W400
                        ),
                        color = Color.White,
                    )
                }

                Text(
                    text = "$onlineDeviceCount DEVICES ACTIVE",
                    color = Color.White,
                    style = AppTheme.typography.titleMedium.copy(
                        fontSize = 12.sp,
                        fontWeight = FontWeight.W700
                    ),
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .background(color = Color(0xFF78736A), shape = CircleShape)
                        .clipWithBorder(shape = CircleShape, borderColor = Color(0x33FFFFFF), borderWidth = 1.dp)
                        .padding(horizontal = 15.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun DeviceItem(
    device: DeviceUiItem,
    isOnline: Boolean,
    switch: Boolean,
    showDeleteIcon: Boolean = true,
    onSwitchChange: (DeviceId, Boolean) -> Unit,
    onConnectClick: (DeviceId) -> Unit,
    onDisconnectClick: (DeviceId) -> Unit,
    onClick: (DeviceId) -> Unit,
    onDeleteClick: (DeviceId) -> Unit
) {
    val colorFilter = remember {
        ColorFilter.colorMatrix(ColorMatrix().apply { setToSaturation(0F) })
    }
    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBackground),
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
                        .padding(if (device.isLdvBedside()) 10.dp else 0.dp)
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
                            .size(24.dp)
                            .debouncedClickable { onConnectClick.invoke(device.deviceId) },
                    )
                } else {
                    MeluceSwitch(
                        checked = switch,
                        onCheckedChange = { onSwitchChange.invoke(device.deviceId, it) },
                        modifier = Modifier.padding(top = 10.dp, end = 10.dp),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, start = 10.dp, end = 10.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text(
                    text = device.name,
                    maxLines = 2,
                    color = AppTheme.colors.title,
                    overflow = TextOverflow.Ellipsis,
                    style = AppTheme.typography.bodyMedium,
                    modifier = Modifier.weight(1f)
                )
                if (showDeleteIcon) {
                    Icon(
                        painter = painterResource(R.drawable.ic_delete),
                        contentDescription = "delete",
                        modifier = Modifier
                            .size(20.dp)
                            .debouncedClickable { onDeleteClick.invoke(device.deviceId) },
                        tint = AppTheme.colors.dialogNegative
                    )
                }
            }
        }
    }
}