package com.ledvance.energy.manager.dialog

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil3.compose.rememberAsyncImagePainter
import com.ledvance.nfc.utils.NfcProgressState
import com.ledvance.ui.R
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2025/6/12 14:14
 * Describe : NfcProgressDialog
 */
@Composable
fun NfcProgressDialog(
    type: NfcOperationType = NfcOperationType.Read,
    progress: NfcProgressState,
    onCloseDialog: () -> Unit,
) {
    val isRead = type == NfcOperationType.Read
    Dialog(onDismissRequest = { }) {
        Column(
            modifier = Modifier
                .background(
                    color = AppTheme.colors.dialogSecondaryBackground,
                    shape = RoundedCornerShape(14.dp)
                )
                .clip(RoundedCornerShape(14.dp)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .height(280.dp)
                    .fillMaxWidth()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(184.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        if (progress == NfcProgressState.Idle) {
                            Image(
                                painter = rememberAsyncImagePainter(R.mipmap.gif_close_to_tag),
                                contentDescription = null,
                                modifier = Modifier
                                    .fillMaxWidth(),
                                contentScale = ContentScale.FillWidth
                            )
                        } else {
                            RotatingImage(
                                res = progress.getIconResId(),
                                rotate = progress.isIconRotate()
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.fillMaxHeight(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        StepProgressIndicator(progress)
                        Text(
                            text = progress.getProgressMessage(isRead),
                            style = AppTheme.typography.labelMedium.copy(fontSize = 14.sp),
                            color = AppTheme.colors.dialogMessage,
                            textAlign = TextAlign.Center,
                            maxLines = 3,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(start = 20.dp, end = 20.dp)
                                .fillMaxWidth()
                        )
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
                IconButton(
                    onClick = { onCloseDialog.invoke() },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(40.dp)
                ) {
                    Image(
                        painter = painterResource(id = R.mipmap.icon_close),
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun StepProgressIndicator(progress: NfcProgressState) {
    if (progress == NfcProgressState.Fail) {
        Spacer(modifier = Modifier.height(39.dp))
        return
    }
    val steps = remember {
        listOf(
            NfcProgressState.Programing,
            NfcProgressState.Verify,
            NfcProgressState.Success
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 28.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        StepDoneIcon()
        HorizontalLine()
        steps.forEachIndexed { index, step ->
            val isDone = progress.ordinal >= step.ordinal
            if (isDone) StepDoneIcon() else StepIncompleteIcon()
            if (index < steps.lastIndex) {
                HorizontalLine()
            }
        }
    }
}

@Composable
private fun StepDoneIcon() {
    Image(
        painter = painterResource(id = R.mipmap.icon_process_done),
        contentDescription = null,
        modifier = Modifier.size(13.dp)
    )
}

@Composable
private fun RowScope.HorizontalLine() {
    HorizontalDivider(
        modifier = Modifier.weight(1f),
        color = Color.LightGray,
        thickness = 1.dp
    )
}

@Composable
private fun StepIncompleteIcon() {
    Surface(
        shape = CircleShape,
        color = AppTheme.colors.divider,
        modifier = Modifier.size(7.2.dp),
        content = {})
}

@Composable
private fun RotatingImage(res: Int, rotate: Boolean = false) {
    val infiniteTransition = rememberInfiniteTransition()
    val angle by infiniteTransition.animateFloat(
        initialValue = 0F,
        targetValue = if (rotate) 360F else 0F,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing)
        )
    )
    Image(
        painter = rememberAsyncImagePainter(res),
        contentDescription = null,
        modifier = Modifier
            .size(90.dp)
            .rotate(angle)
    )
}

private fun NfcProgressState.getIconResId(): Int = when (this) {
    NfcProgressState.Idle -> R.mipmap.gif_close_to_tag
    NfcProgressState.Programing -> R.mipmap.icon_process_loading
    NfcProgressState.Verify -> R.mipmap.icon_process_loading
    NfcProgressState.Success -> R.mipmap.icon_process_success
    NfcProgressState.Fail -> R.mipmap.icon_process_fail
}

@Composable
private fun NfcProgressState.getProgressMessage(isRead: Boolean): String {
    val messageResId = when (this) {
        NfcProgressState.Idle -> R.string.nfc_connect_tips
        NfcProgressState.Programing -> if (isRead) R.string.nfc_operation_read_progress_programming
        else R.string.nfc_operation_write_progress_programming

        NfcProgressState.Verify -> R.string.nfc_operation_common_progress_verify
        NfcProgressState.Success -> if (isRead) R.string.nfc_operation_read_progress_success
        else R.string.nfc_operation_write_progress_success

        NfcProgressState.Fail -> if (isRead) R.string.nfc_operation_read_progress_fail
        else R.string.nfc_operation_write_progress_fail
    }
    val index = when (this) {
        NfcProgressState.Idle -> "1."
        NfcProgressState.Programing -> "2."
        NfcProgressState.Verify -> "3."
        NfcProgressState.Success -> "4."
        NfcProgressState.Fail -> ""
    }
    return "$index${stringResource(messageResId)}"
}

private fun NfcProgressState.isIconRotate(): Boolean = when (this) {
    NfcProgressState.Programing, NfcProgressState.Verify -> true
    else -> false
}

sealed interface NfcOperationType {
    data object Read : NfcOperationType
    data object Write : NfcOperationType
}