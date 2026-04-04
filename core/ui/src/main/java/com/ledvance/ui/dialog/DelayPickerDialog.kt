package com.ledvance.ui.dialog

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.ledvance.ui.component.DelayPicker

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/4/4 13:56
 * Describe : DelayPickerDialog wrapping DelayPicker in a ModalBottomSheet
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DelayPickerDialog(
    minutes: Int,
    visible: Boolean,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit,
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            sheetGesturesEnabled = false,
            dragHandle = null
        ) {
            DelayPicker(
                initialMinutes = minutes,
                onCancel = onDismiss,
                onConfirm = onConfirm
            )
        }
    }
}
