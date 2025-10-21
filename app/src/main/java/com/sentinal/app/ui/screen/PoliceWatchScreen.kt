package com.sentinal.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PoliceWatchScreen(
    onBack: () -> Unit = {},
    onArm: () -> Unit = {}
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Police Watch") },
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
            Text("Rapid-response profile", style = MaterialTheme.typography.titleLarge)

            var autoRecord by remember { mutableStateOf(true) }
            var autoTextPrimaryContact by remember { mutableStateOf(true) }
            var loudAlarm by remember { mutableStateOf(false) }

            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Auto-record on trigger")
                Switch(checked = autoRecord, onCheckedChange = { autoRecord = it })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Text Contact #1 on trigger")
                Switch(checked = autoTextPrimaryContact, onCheckedChange = { autoTextPrimaryContact = it })
            }
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Play audible alarm")
                Switch(checked = loudAlarm, onCheckedChange = { loudAlarm = it })
            }

            Spacer(Modifier.weight(1f))

            Button(
                onClick = onArm,
                modifier = Modifier.fillMaxWidth()
            ) { Text("Arm Police Watch") }
        }
    }
}
