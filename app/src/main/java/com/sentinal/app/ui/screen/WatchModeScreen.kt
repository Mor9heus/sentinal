package com.sentinal.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun WatchModeScreen(
    onBack: () -> Unit = {},
    onStartWatch: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Watch Mode") },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("Back") }
                }
            )
        }
    ) { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Overview", style = MaterialTheme.typography.titleLarge)
            Text(
                "Watch Mode keeps an eye on your environment. Toggle options and start when ready.",
                style = MaterialTheme.typography.bodyMedium
            )

            var livePings by remember { mutableStateOf(true) }
            var geoCheck by remember { mutableStateOf(true) }
            var lowPower by remember { mutableStateOf(false) }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Live pings")
                Switch(checked = livePings, onCheckedChange = { livePings = it })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Geofence check")
                Switch(checked = geoCheck, onCheckedChange = { geoCheck = it })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Low-power mode")
                Switch(checked = lowPower, onCheckedChange = { lowPower = it })
            }

            Spacer(Modifier.weight(1f))
            Button(
                onClick = onStartWatch,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Start Watch") }
        }
    }
}
