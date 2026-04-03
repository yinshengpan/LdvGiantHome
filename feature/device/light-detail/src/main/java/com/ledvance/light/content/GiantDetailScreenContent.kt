package com.ledvance.light.content

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.ledvance.domain.bean.DeviceType
import com.ledvance.domain.bean.WorkMode
import com.ledvance.light.LightControlView
import com.ledvance.light.LightDetailsContract
import com.ledvance.light.component.CardFeature
import com.ledvance.light.component.CardFeatureItem

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/18/26 15:35
 * Describe : GiantDetailScreenContent
 */
@Composable
internal fun GiantDetailScreenContent(
    deviceType: DeviceType,
    uiState: LightDetailsContract.DetailState.GiantDetailState,
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
            isSupportWhiteMode = deviceType == DeviceType.GiantTable,
            whiteModeBrightness = uiState.whiteModeBrightness,
            onSwitchChange = onSwitchChange,
            onWorkModeChange = onWorkModeChange,
            onColourModeHsChange = onColourModeHsChange,
            onColourModeBrightnessChange = onColourModeBrightnessChange,
            onWhiteModeCctChange = onWhiteModeCctChange,
            onWhiteModeBrightnessChange = onWhiteModeBrightnessChange,
        )
        FlowRow(
            maxItemsInEachRow = 2,
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(15.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
        ) {
            uiState.cardFeatureList.forEach { feature ->
                CardFeatureItem(cardFeature = feature, modifier = Modifier.weight(1f)) {
                    onNavigateToFeature(feature)
                }
            }
            val isOddCount = uiState.cardFeatureList.size % 2 != 0
            if (isOddCount) {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}