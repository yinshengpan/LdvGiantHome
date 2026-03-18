package com.ledvance.home.control

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.ledvance.ui.component.LedvanceScreen
import com.ledvance.ui.theme.AppTheme

@Composable
fun ControlPanelScreen(
    onNavigateBack: () -> Unit,
    viewModel: ControlPanelViewModel = hiltViewModel()
) {
    var brightness by remember { mutableFloatStateOf(100f) }
    var temperature by remember { mutableFloatStateOf(50f) }
    var hue by remember { mutableFloatStateOf(180f) }
    var saturation by remember { mutableFloatStateOf(50f) }

    LedvanceScreen(
        onBackPressed = onNavigateBack,
        title = "Device Control Panel",
        topBarContainerColor = AppTheme.colors.primaryBackground,
        topBarContentColor = AppTheme.colors.primaryContent,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Power", style = AppTheme.typography.titleMedium, color = AppTheme.colors.title)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { viewModel.on() }) { Text("Turn ON") }
                Button(onClick = { viewModel.off() }) { Text("Turn OFF") }
            }

            Divider()

            Text(text = "White Light", style = AppTheme.typography.titleMedium, color = AppTheme.colors.title)
            Card(
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.screenBackground),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Brightness: ${brightness.toInt()}%", color = AppTheme.colors.title)
                    Slider(
                        value = brightness,
                        onValueChange = { brightness = it },
                        valueRange = 0f..100f,
                        onValueChangeFinished = {
                            viewModel.setCCT(temperature.toInt(), brightness.toInt())
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Temperature: ${temperature.toInt()}%", color = AppTheme.colors.title)
                    Slider(
                        value = temperature,
                        onValueChange = { temperature = it },
                        valueRange = 0f..100f,
                        onValueChangeFinished = {
                            viewModel.setCCT(temperature.toInt(), brightness.toInt())
                        }
                    )
                }
            }

            Divider()

            Text(text = "Color Light", style = AppTheme.typography.titleMedium, color = AppTheme.colors.title)
            Card(
                colors = CardDefaults.cardColors(containerColor = AppTheme.colors.screenBackground),
                elevation = CardDefaults.cardElevation(2.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Hue: ${hue.toInt()}", color = AppTheme.colors.title)
                    Slider(
                        value = hue,
                        onValueChange = { hue = it },
                        valueRange = 0f..360f,
                        onValueChangeFinished = {
                            viewModel.setHSV(hue.toInt(), saturation.toInt(), brightness.toInt())
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Saturation: ${saturation.toInt()}%", color = AppTheme.colors.title)
                    Slider(
                        value = saturation,
                        onValueChange = { saturation = it },
                        valueRange = 0f..100f,
                        onValueChangeFinished = {
                            viewModel.setHSV(hue.toInt(), saturation.toInt(), brightness.toInt())
                        }
                    )
                }
            }

            Divider()

            Text(text = "Scenes", style = AppTheme.typography.titleMedium, color = AppTheme.colors.title)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(modifier = Modifier.weight(1f), onClick = { viewModel.setScene(1) }) { Text("Read") }
                Button(modifier = Modifier.weight(1f), onClick = { viewModel.setScene(2) }) { Text("Relax") }
                Button(modifier = Modifier.weight(1f), onClick = { viewModel.setScene(3) }) { Text("Party") }
            }
        }
    }
}
