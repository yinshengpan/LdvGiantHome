package com.ledvance.light.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyHorizontalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ledvance.domain.bean.command.scenes.Scene
import com.ledvance.domain.bean.command.scenes.TableSceneType
import com.ledvance.domain.bean.command.scenes.TableScenes
import com.ledvance.light.bean.ScenesSegment
import com.ledvance.ui.R
import com.ledvance.ui.component.IRadioGroupItem
import com.ledvance.ui.component.LedvanceRadioGroup
import com.ledvance.ui.extensions.debouncedClickable
import com.ledvance.ui.theme.AppTheme

/**
 * @author : jason yin
 * Email : j.yin@ledvance.com
 * Created date 3/19/26 17:39
 * Describe : ScenesControl
 */
@Composable
fun ScenesControl(onClickScene: (Scene) -> Unit) {
    val allScenesSegment = remember { ScenesSegment.allScenesSegment }
    var selectedSceneSegment by remember {
        mutableStateOf<IRadioGroupItem<TableSceneType>>(ScenesSegment.Natural)
    }
    val tableScenes = remember {
        TableScenes.tableScenes
    }

    val scenes by remember(selectedSceneSegment) {
        mutableStateOf(tableScenes[selectedSceneSegment.value] ?: emptyList())
    }

    Card(
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = AppTheme.colors.screenBackground),
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier.padding(paddingValues = PaddingValues(20.dp)),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp)
        ) {
            LedvanceRadioGroup(
                selectorItem = selectedSceneSegment,
                items = allScenesSegment,
                modifier = Modifier.padding(top = 15.dp, bottom = 10.dp),
                shape = RoundedCornerShape(8.dp),
                checkedColor = Color.White,
                backgroundColor = AppTheme.colors.divider,
                checkedTextColor = AppTheme.colors.title,
                textColor = AppTheme.colors.title,
                onCheckedChange = {
                    selectedSceneSegment = it
                }
            )
            LazyHorizontalGrid(rows = GridCells.Fixed(1), modifier = Modifier.height(100.dp)) {
                items(scenes) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .debouncedClickable(onClick = {
                                onClickScene.invoke(it)
                            }),
                    ) {
                        Image(
                            painter = painterResource(R.mipmap.colorful_icon),
                            contentDescription = null,
                            modifier = Modifier.size(44.dp)
                        )
                        Text(
                            text = it.title,
                            style = AppTheme.typography.bodySmall,
                            color = AppTheme.colors.title,
                            maxLines = 2,
                            textAlign = TextAlign.Center,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier
                                .padding(top = 10.dp)
                                .width(50.dp)
                        )
                    }
                }
            }
        }
    }
}