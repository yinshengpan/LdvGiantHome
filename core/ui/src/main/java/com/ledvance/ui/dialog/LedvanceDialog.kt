package com.ledvance.ui.dialog

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import com.ledvance.ui.R
import com.ledvance.ui.extensions.clickableNonRipples
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2023/11/23 15:21
 * Describe : Dialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedvanceBottomSheetDialog(
    modifier: Modifier = Modifier,
    confirmText: String = stringResource(id = R.string.confirm),
    cancelText: String = stringResource(id = R.string.cancel),
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    BasicAlertDialog(
        modifier = modifier.fillMaxWidth(),
        properties = DialogProperties(
            dismissOnClickOutside = true,
            usePlatformDefaultWidth = false,
        ),
        onDismissRequest = { onCancel?.invoke() },
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent),
            contentAlignment = Alignment.BottomCenter
        ) {
            Column(modifier = Modifier.fillMaxWidth()) {
                content()
                onConfirm?.also {
                    Text(
                        text = confirmText,
                        textAlign = TextAlign.Center,
                        style = AppTheme.typography.bodyLarge,
                        color = AppTheme.colors.dialogPositive,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 15.dp, end = 15.dp, top = 10.dp)
                            .background(Color.White, shape = RoundedCornerShape(7.dp))
                            .padding(vertical = 14.dp)
                            .clickableNonRipples {
                                onConfirm()
                            })
                }

                onCancel?.also {
                    Text(
                        text = cancelText,
                        textAlign = TextAlign.Center,
                        style = AppTheme.typography.bodyLarge,
                        color = AppTheme.colors.dialogNegative,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 28.dp, start = 15.dp, end = 15.dp, top = 10.dp)
                            .background(Color.White, shape = RoundedCornerShape(7.dp))
                            .padding(vertical = 14.dp)
                            .clickableNonRipples {
                                onCancel()
                            })
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LedvanceDialog(
    modifier: Modifier = Modifier,
    title: String? = null,
    message: String,
    maxLines: Int = 3,
    cancelText: String? = stringResource(R.string.cancel),
    confirmText: String = stringResource(R.string.confirm),
    cancelTextColor: Color = AppTheme.colors.dialogNegative,
    confirmTextColor: Color = AppTheme.colors.dialogPositive,
    onConfirm: (() -> Unit)? = null,
    onCancel: (() -> Unit)? = null,
) {
    BasicAlertDialog(
        modifier = modifier.fillMaxWidth(),
        properties = DialogProperties(
            dismissOnClickOutside = false,
            usePlatformDefaultWidth = false,
            dismissOnBackPress = false
        ),
        onDismissRequest = { onCancel?.invoke() },
    ) {
        Card(
            modifier = Modifier
                .padding(vertical = 20.dp, horizontal = 30.dp)
                .fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 7.dp),
            colors = CardDefaults.cardColors(
                containerColor = AppTheme.colors.dialogBackground
            ),
            shape = RoundedCornerShape(14.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                title?.also {
                    Text(
                        text = it,
                        style = AppTheme.typography.titleLarge.copy(fontSize = 17.sp),
                        color = AppTheme.colors.dialogTitle,
                        textAlign = TextAlign.Center,
                        overflow = TextOverflow.Ellipsis,
                        maxLines = 1,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                            .padding(bottom = 8.dp)

                    )
                }
                Text(
                    text = message,
                    style = AppTheme.typography.bodyLarge.copy(fontSize = 13.sp),
                    color = AppTheme.colors.dialogMessage,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    maxLines = maxLines,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                        .padding(bottom = 15.dp)
                )
                HorizontalDivider(thickness = 0.5.dp, color = AppTheme.colors.divider)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (!cancelText.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .debouncedClickable {
                                    onCancel?.invoke()
                                },
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = cancelText,
                                textAlign = TextAlign.Center,
                                style = AppTheme.typography.bodyMedium.copy(fontSize = 17.sp),
                                color = cancelTextColor,
                            )
                        }
                        VerticalDivider(
                            thickness = 0.5.dp,
                            color = AppTheme.colors.divider,
                            modifier = Modifier.fillMaxHeight(),
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .debouncedClickable {
                                onConfirm?.invoke()
                            },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = confirmText,
                            textAlign = TextAlign.Center,
                            style = AppTheme.typography.titleMedium.copy(fontSize = 17.sp),
                            color = confirmTextColor,
                        )
                    }
                }
            }
        }
    }
}