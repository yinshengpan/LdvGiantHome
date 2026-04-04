package com.ledvance.light.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.domain.bean.command.common.ModeType
import com.ledvance.ui.CardView
import com.ledvance.ui.R
import com.ledvance.ui.component.MeluceSwitch
import com.ledvance.ui.extensions.getNameResId
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/3/26 11:46
 * Describe : LdvDeviceHeaderCard
 */

@Composable
internal fun LdvDeviceHeaderCard(
    mode: ModeType,
    deviceName: String,
    deviceIcon: Painter,
    switch: Boolean,
    onSwitchChange: (Boolean) -> Unit,
) {
    CardView(paddingValues = PaddingValues(vertical = 23.dp)) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(295.4.dp),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(R.mipmap.bg_card),
                contentScale = ContentScale.FillBounds,
                contentDescription = null,
                modifier = Modifier.fillMaxSize()
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 23.dp, bottom = 17.5.dp, start = 18.dp, end = 18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Row(
                        modifier = Modifier
                            .background(color = Color(0x33FF976E), shape = CircleShape)
                            .padding(horizontal = 14.8.dp, vertical = 5.5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.4.dp)
                                .background(brush = AppTheme.colors.cardBackgroundBrush, shape = CircleShape)
                        )
                        Text(
                            text = "${stringResource(mode.getNameResId())} Active",
                            style = AppTheme.typography.bodyMedium.copy(fontSize = 11.sp),
                            modifier = Modifier.padding(start = 7.4.dp)
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    MeluceSwitch(checked = switch, onCheckedChange = onSwitchChange)
                }

                Image(
                    painter = deviceIcon,
                    contentDescription = null,
                    contentScale = ContentScale.FillWidth,
                    modifier = Modifier
                        .padding(top = 24.9.dp)
                        .size(123.7.dp)
                )

                Text(
                    text = "Bedside Lamp",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = AppTheme.typography.titleLarge.copy(fontSize = 25.8.sp, fontWeight = FontWeight.W700),
                    modifier = Modifier
                        .padding(top = 23.dp)
                        .padding(horizontal = 20.dp)
                )

                Text(
                    text = "Quick Presets",
                    color = Color(0xFF615E5E),
                    style = AppTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    modifier = Modifier.padding(top = 9.dp)
                )
            }
        }
    }
}