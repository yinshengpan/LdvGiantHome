package com.ledvance.ui.dialog

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.ledvance.ui.component.WeekPicker
import java.time.DayOfWeek

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/3/26 13:37
 * Describe : WeekPickerDialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WeekPickerDialog(
    days: Set<DayOfWeek>,
    visible: Boolean,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    onConfirm: (Set<DayOfWeek>) -> Unit,
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            sheetGesturesEnabled = false,
            dragHandle = null
        ) {
            WeekPicker(
                initialDays = days,
                onCancel = onDismiss,
                onConfirm = { days ->
                    onConfirm(days)
                }
            )
        }
    }
}