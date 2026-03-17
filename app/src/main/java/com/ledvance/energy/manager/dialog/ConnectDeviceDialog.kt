package com.ledvance.energy.manager.dialog

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.ledvance.ui.R
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 11/14/25 10:07
 * Describe : DLMSNDialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConnectDeviceDialog(
    modifier: Modifier = Modifier,
    onDismissRequest: () -> Unit = {},
    onNFCDetection: () -> Unit,
    onScanQRCode: () -> Unit,
) {
    BasicAlertDialog(
        modifier = modifier.fillMaxWidth(),
        properties = DialogProperties(
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
        onDismissRequest = onDismissRequest,
    ) {
        Card(
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 50.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppTheme.colors.dialogBackground
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {

                Text(
                    text = stringResource(R.string.connect_device_dialog_title),
                    style = AppTheme.typography.titleMedium.copy(
                        fontSize = 17.sp,
                        fontWeight = FontWeight.W600
                    ),
                    color = AppTheme.colors.dialogTitle,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 20.dp, start = 16.dp, end = 16.dp)
                )
                Text(
                    text = stringResource(R.string.connect_device_dialog_content),
                    style = AppTheme.typography.bodyMedium.copy(fontSize = 13.sp),
                    color = AppTheme.colors.dialogMessage,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 6.dp, bottom = 20.dp)
                        .padding(horizontal = 16.dp),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                HorizontalDivider(color = AppTheme.colors.divider, thickness = 0.5.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .debouncedClickable {
                            onNFCDetection.invoke()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.nfc_detection),
                        textAlign = TextAlign.Center,
                        style = AppTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
                        color = AppTheme.colors.dialogPositive,
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                HorizontalDivider(color = AppTheme.colors.divider, thickness = 0.5.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .debouncedClickable {
                            onScanQRCode.invoke()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.scan_qr_code),
                        textAlign = TextAlign.Center,
                        style = AppTheme.typography.bodyLarge,
                        color = AppTheme.colors.dialogPositive,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp)
                    )
                }

                HorizontalDivider(color = AppTheme.colors.divider, thickness = 0.5.dp)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .debouncedClickable {
                            onDismissRequest.invoke()
                        },
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = stringResource(R.string.cancel),
                        textAlign = TextAlign.Center,
                        style = AppTheme.typography.bodyLarge,
                        color = AppTheme.colors.dialogNegative,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                    )
                }
            }
        }
    }
}