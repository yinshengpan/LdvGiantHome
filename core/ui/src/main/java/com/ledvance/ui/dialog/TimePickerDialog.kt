package com.ledvance.ui.dialog

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.ledvance.ui.component.TimePicker

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/3/26 13:37
 * Describe : TimePickerDialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    hour: Int,
    minute: Int,
    visible: Boolean,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    onConfirm: (Int, Int) -> Unit,
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            sheetGesturesEnabled = false,
            dragHandle = null
        ) {
            TimePicker(
                initialHour = hour,
                initialMinute = minute,
                onCancel = onDismiss,
                onConfirm = onConfirm
            )
        }
    }
}