package com.ledvance.scene

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ledvance.domain.bean.command.giant.scenes.Scene
import com.ledvance.ui.component.BrightnessSlider
import com.ledvance.ui.component.LedvanceRadioGroup
import com.ledvance.ui.component.SpeedSlider
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.extensions.getIconResId
import com.ledvance.ui.extensions.getNameResId
import com.ledvance.ui.segment.SceneSegment
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 17:39
 * Describe : ScenesScreenContent
 */
@Composable
internal fun ScenesScreenContent(
    uiState: ScenesContract.UiState.Success,
    onSceneSegmentChange: (SceneSegment) -> Unit,
    onSceneChange: (Scene) -> Unit,
    onSpeedChange: (Int) -> Unit,
    onBrightnessChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = Modifier.padding(horizontal = 20.dp)) {
        Card(
            elevation = CardDefaults.cardElevation(8.dp),
            colors = CardDefaults.cardColors(containerColor = AppTheme.colors.screenBackground),
            shape = RoundedCornerShape(10.dp),
            modifier = modifier.padding(paddingValues = PaddingValues(vertical = 20.dp)),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(15.dp)
            ) {
                BrightnessSlider(
                    brightness = uiState.brightness,
                    onBrightnessChange = onBrightnessChange,
                )
                SpeedSlider(
                    modifier = Modifier.padding(top = 15.dp),
                    speed = uiState.speed,
                    onSpeedChange = onSpeedChange,
                )

                if (uiState.sceneSegments.isNotEmpty()) {
                    LedvanceRadioGroup(
                        selectorItem = uiState.selectedSceneSegment,
                        items = uiState.sceneSegments,
                        modifier = Modifier.padding(top = 15.dp, bottom = 10.dp),
                        shape = RoundedCornerShape(8.dp),
                        checkedColor = Color.White,
                        backgroundColor = AppTheme.colors.divider,
                        checkedTextColor = AppTheme.colors.title,
                        textColor = AppTheme.colors.title,
                        onCheckedChange = {
                            if (it is SceneSegment) {
                                onSceneSegmentChange.invoke(it)
                            }
                        }
                    )
                } else {
                    Spacer(modifier = Modifier.height(15.dp))
                }
                LazyVerticalGrid(
                    columns = GridCells.Fixed(4),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(uiState.scenes) {
                        SceneItem(
                            scene = it,
                            isSelected = it == uiState.selectedScene,
                            onItemClick = onSceneChange
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SceneItem(scene: Scene, isSelected: Boolean, onItemClick: (Scene) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .debouncedClickable(onClick = {
                onItemClick.invoke(scene)
            }),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(scene.getIconResId()),
            contentDescription = null,
            modifier = Modifier
                .size(54.dp)
                .then(
                    other = if (isSelected) {
                        Modifier.border(
                            width = 2.dp,
                            color = AppTheme.colors.primary,
                            shape = CircleShape
                        )
                    } else {
                        Modifier
                    }
                )
        )
        Text(
            text = stringResource(scene.getNameResId()),
            style = AppTheme.typography.bodySmall,
            color = if (isSelected) AppTheme.colors.primary else AppTheme.colors.title,
            maxLines = 2,
            textAlign = TextAlign.Center,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 10.dp)
        )
    }
}
