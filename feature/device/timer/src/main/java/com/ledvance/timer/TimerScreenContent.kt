package com.ledvance.timer

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ledvance.domain.bean.TimerUiItem
import com.ledvance.ui.CardView
import com.ledvance.ui.R
import com.ledvance.ui.component.ScheduleSettingCard
import com.ledvance.ui.extensions.getNameResId
import timber.log.Timber

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 18:15
 * Describe : TimerScreenContent
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun TimerScreenContent(
    uiState: TimerContract.UiState.Success,
    modifier: Modifier = Modifier,
    onTimerChange: (TimerUiItem) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .then(modifier),
    ) {
        Spacer(modifier = Modifier.height(12.5.dp))
        uiState.timerList.forEach { timer ->
            CardView(paddingValues = PaddingValues(vertical = 12.5.dp, horizontal = 20.dp)) {
                ScheduleSettingCard(
                    title = stringResource(timer.timerType.getNameResId()),
                    icon = painterResource(R.mipmap.icon_timer),
                    switch = timer.enabled,
                    onSwitchChange = { onTimerChange(timer.copy(enabled = it)) },
                    hour = timer.hour,
                    minute = timer.minute,
                    displayTime = timer.displayTime,
                    onTimeChange = { h, m -> onTimerChange(timer.copy(hour = h, minute = m)) },
                    repeatDays = timer.days,
                    displayRepeat = timer.displayRepeat,
                    onRepeatChange = { days -> onTimerChange(timer.copy(days = days)) },
                    delay = timer.delay,
                    displayDelay = "${timer.delay} min",
                    onDelayChange = { delay -> onTimerChange(timer.copy(delay = delay)) }
                )
            }
        }
        Spacer(modifier = Modifier.height(12.5.dp))
    }

}
