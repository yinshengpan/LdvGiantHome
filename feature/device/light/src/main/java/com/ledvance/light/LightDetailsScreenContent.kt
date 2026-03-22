package com.ledvance.light

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ledvance.domain.bean.WorkMode
import com.ledvance.light.component.CardFeature
import com.ledvance.light.component.CardFeatureItem
import com.ledvance.light.component.LightControlView

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 15:35
 * Describe : LightDetailsScreenContent
 */
@Composable
internal fun LightDetailsScreenContent(
    uiState: LightDetailsContract.UiState.Success,
    onSwitchChange: (Boolean) -> Unit,
    onWorkModeChange: (WorkMode) -> Unit,
    onColourModeHsChange: (Int, Int) -> Unit,
    onColourModeBrightnessChange: (Int) -> Unit,
    onWhiteModeCctChange: (Int) -> Unit,
    onWhiteModeBrightnessChange: (Int) -> Unit,
    onNavigateToFeature: (CardFeature) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
    ) {
        LightControlView(
            switch = uiState.power,
            workMode = uiState.workMode,
            colourModeHue = uiState.colourModeHue,
            colourModeSat = uiState.colourModeSat,
            colourModeBrightness = uiState.colourModeBrightness,
            whiteModeCct = uiState.whiteModeCct,
            whiteModeBrightness = uiState.whiteModeBrightness,
            onSwitchChange = onSwitchChange,
            onWorkModeChange = onWorkModeChange,
            onColourModeHsChange = onColourModeHsChange,
            onColourModeBrightnessChange = onColourModeBrightnessChange,
            onWhiteModeCctChange = onWhiteModeCctChange,
            onWhiteModeBrightnessChange = onWhiteModeBrightnessChange,
        )
        if (uiState.power) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2), modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(15.dp),
            ) {
                items(uiState.cardFeatureList) { feature ->
                    CardFeatureItem(cardFeature = feature) {
                        onNavigateToFeature(feature)
                    }
                }
            }
        }
    }
}