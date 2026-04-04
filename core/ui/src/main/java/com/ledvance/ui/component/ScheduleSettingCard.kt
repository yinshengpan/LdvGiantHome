package com.ledvance.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ledvance.ui.R
import com.ledvance.ui.dialog.DelayPickerDialog
import com.ledvance.ui.dialog.TimePickerDialog
import com.ledvance.ui.dialog.WeekPickerDialog
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme
import java.time.DayOfWeek

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 4/3/26 13:47
 * Describe : ScheduleSettingCard
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleSettingCard(
    title: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = AppTheme.colors.cardBackground,
    icon: Painter? = null,
    switch: Boolean,
    onSwitchChange: (Boolean) -> Unit,
    hour: Int,
    minute: Int,
    displayTime: String,
    onTimeChange: (Int, Int) -> Unit,
    repeatDays: Set<DayOfWeek>,
    disabledRepeatDays: Set<DayOfWeek> = emptySet(),
    displayRepeat: String,
    onRepeatChange: (Set<DayOfWeek>) -> Unit,
    delay: Int = 0,
    displayDelay: String? = null,
    onDelayChange: (Int) -> Unit = {},
) {
    var showRepeatPicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showDelayPicker by remember { mutableStateOf(false) }

    WeekPickerDialog(
        visible = showRepeatPicker,
        days = repeatDays,
        disabledDays = disabledRepeatDays,
        onDismiss = { showRepeatPicker = false },
        onConfirm = {
            onRepeatChange(it)
            showRepeatPicker = false
        }
    )

    TimePickerDialog(
        visible = showTimePicker,
        hour = hour,
        minute = minute,
        onDismiss = { showTimePicker = false },
        onConfirm = { h, m ->
            onTimeChange(h, m)
            showTimePicker = false
        }
    )

    DelayPickerDialog(
        visible = showDelayPicker,
        minutes = delay,
        onDismiss = { showDelayPicker = false },
        onConfirm = { m ->
            onDelayChange(m)
            showDelayPicker = false
        }
    )

    Column(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(10.dp)
            ),
    ) {
        ScheduleSettingItem(
            title = title,
            icon = icon,
            switch = switch,
            onSwitchChange = onSwitchChange
        )
        if (icon != null) {
            HorizontalDivider(
                color = Color(0xFFDDDDDD),
                thickness = 1.dp
            )
        }
        ScheduleSettingItem(
            title = stringResource(R.string.timer_time),
            content = displayTime,
            onContentClick = {
                showTimePicker = true
            }
        )
        if (!displayDelay.isNullOrEmpty()) {
            ScheduleSettingItem(
                title = stringResource(R.string.timer_delay),
                content = displayDelay,
                onContentClick = {
                    showDelayPicker = true
                }
            )
        }
        ScheduleSettingItem(
            title = stringResource(R.string.timer_repeat),
            content = displayRepeat,
            onContentClick = {
                showRepeatPicker = true
            },
        )
    }
}

@Composable
private fun ScheduleSettingItem(
    title: String,
    icon: Painter? = null,
    content: String? = null,
    switch: Boolean? = null,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onContentClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 15.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        icon?.also {
            Image(
                painter = icon,
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 9.dp)
                    .size(37.dp)
                    .background(color = Color(0x4DFF976E), shape = CircleShape)
                    .padding(7.dp)
            )
        }
        Text(
            text = title, style = AppTheme.typography.titleSmall,
            color = AppTheme.colors.title,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        when {
            switch != null && onSwitchChange != null -> {
                MeluceSwitch(switch, onSwitchChange)
            }

            content != null -> {
                Text(
                    text = content,
                    style = AppTheme.typography.bodyMedium,
                    color = AppTheme.colors.primary,
                    modifier = Modifier.debouncedClickable(onClick = {
                        onContentClick?.invoke()
                    })
                )
            }
        }

        if (onContentClick != null) {
            Image(
                painter = painterResource(R.drawable.ic_arrow_right),
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 4.dp)
                    .size(11.dp),
                colorFilter = ColorFilter.tint(Color(0xFF979797))
            )
        }
    }
}