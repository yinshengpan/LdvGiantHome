package com.ledvance.room

import androidx.compose.runtime.Composable
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ledvance.ui.component.LedvanceScreen

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 10:53
 * Describe : RoomScreen
 */
@Composable
internal fun RoomScreen(
    viewModel: RoomContract = hiltViewModel<RoomViewModel>(),
) {
    LedvanceScreen() {

    }
}