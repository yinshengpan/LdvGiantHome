package com.ledvance.setting.dialog

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import com.ledvance.domain.bean.command.giant.LineSequence
import com.ledvance.setting.LineSequencePicker
import com.ledvance.ui.component.WeekPicker
import java.time.DayOfWeek

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 2026/4/6 14:42
 * Describe : LineSequencePickerDialog
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LineSequencePickerDialog(
    visible: Boolean,
    lineSequence: LineSequence? = null,
    sheetState: SheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
    onDismiss: () -> Unit,
    onConfirm: (LineSequence) -> Unit,
) {
    if (visible) {
        ModalBottomSheet(
            onDismissRequest = onDismiss,
            sheetState = sheetState,
            sheetGesturesEnabled = false,
            dragHandle = null
        ) {
            LineSequencePicker(
                initialSequence = lineSequence,
                onCancel = onDismiss,
                onConfirm = {
                    onConfirm(it)
                }
            )
        }
    }
}