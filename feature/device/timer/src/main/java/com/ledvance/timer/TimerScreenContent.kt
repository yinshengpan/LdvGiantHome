package com.ledvance.timer

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ledvance.domain.bean.TimerUiItem
import com.ledvance.domain.bean.command.common.TimerType
import com.ledvance.ui.R
import com.ledvance.ui.component.MeluceSwitch
import com.ledvance.ui.component.ScheduleSettingCard
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.extensions.getFullNameResId
import com.ledvance.ui.theme.AppTheme
import java.time.DayOfWeek

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 18:15
 * Describe : TimerScreenContent
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimerScreenContent(
    onTimer: TimerUiItem,
    offTimer: TimerUiItem,
    onTimerSwitchChange: (TimerType, Boolean) -> Unit,
    onTimerTimeChange: (TimerType, Int, Int) -> Unit,
    onTimerRepeatChange: (TimerType, Set<DayOfWeek>) -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.cardBackground),
        shape = RoundedCornerShape(10.dp),
        modifier = modifier.padding(paddingValues = PaddingValues(20.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {

            ScheduleSettingCard(
                backgroundColor = AppTheme.colors.screenSecondaryBackground,
                title = stringResource(R.string.timer_regularly_on),
                switch = onTimer.enabled,
                onSwitchChange = { onTimerSwitchChange(TimerType.GiantOn, it) },
                hour = onTimer.hour,
                minute = onTimer.minute,
                displayTime = onTimer.displayTime,
                onTimeChange = { h, m ->
                    onTimerTimeChange(TimerType.GiantOn, h, m)
                },
                repeatDays = onTimer.days,
                displayRepeat = onTimer.displayRepeat,
                onRepeatChange = { days -> onTimerRepeatChange(TimerType.GiantOn, days) },
            )

            ScheduleSettingCard(
                backgroundColor = AppTheme.colors.screenSecondaryBackground,
                modifier = Modifier.padding(top = 10.dp),
                title = stringResource(R.string.timer_regularly_off),
                switch = offTimer.enabled,
                onSwitchChange = { onTimerSwitchChange(TimerType.GiantOff, it) },
                hour = offTimer.hour,
                minute = offTimer.minute,
                displayTime = offTimer.displayTime,
                onTimeChange = { h, m ->
                    onTimerTimeChange(TimerType.GiantOff, h, m)
                },
                repeatDays = offTimer.days,
                displayRepeat = offTimer.displayRepeat,
                onRepeatChange = { days -> onTimerRepeatChange(TimerType.GiantOff, days) },
            )
        }
    }
}

@Composable
private fun RepeatPicker(
    initialDays: Set<DayOfWeek>,
    onCancel: () -> Unit,
    onConfirm: (Set<DayOfWeek>) -> Unit
) {
    var selectedDays by remember { mutableStateOf(initialDays) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White, RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.dialog_select_time_repeat_title),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        val daysOfWeek = DayOfWeek.entries
        LazyColumn(modifier = Modifier.wrapContentHeight()) {
            items(daysOfWeek) { day ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            selectedDays = if (selectedDays.contains(day)) {
                                selectedDays - day
                            } else {
                                selectedDays + day
                            }
                        }
                        .padding(vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(day.getFullNameResId()),
                        modifier = Modifier.weight(1f),
                        color = Color.Black
                    )
                    Icon(
                        painter = painterResource(R.drawable.ic_check_circle),
                        contentDescription = null,
                        tint = if (selectedDays.contains(day)) AppTheme.colors.primary else AppTheme.colors.divider
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onCancel,
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFF2F2F2), contentColor = Color.Black),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(text = stringResource(R.string.cancel))
            }

            Button(
                onClick = { onConfirm(selectedDays) },
                modifier = Modifier
                    .weight(1f)
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AppTheme.colors.primary, contentColor = Color.White),
                shape = RoundedCornerShape(28.dp)
            ) {
                Text(text = stringResource(R.string.confirm))
            }
        }
    }
}

@Composable
private fun TimerItem(
    title: String,
    time: String,
    onTimeClick: () -> Unit,
    repeat: String,
    onRepeatClick: () -> Unit,
    switch: Boolean,
    onSwitchChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = Modifier
            .then(modifier)
            .fillMaxWidth()
            .background(
                color = AppTheme.colors.screenSecondaryBackground,
                shape = RoundedCornerShape(4.dp)
            ),
    ) {
        Item(title = title, switch = switch, onSwitchChange = onSwitchChange)
        Item(title = stringResource(R.string.timer_time), content = time, onContentClick = onTimeClick)
        Item(title = stringResource(R.string.timer_repeat), content = repeat, onContentClick = onRepeatClick)
    }
}

@Composable
private fun Item(
    title: String,
    content: String? = null,
    switch: Boolean? = null,
    onSwitchChange: ((Boolean) -> Unit)? = null,
    onContentClick: (() -> Unit)? = null,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp)
            .height(46.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
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
    }
}
