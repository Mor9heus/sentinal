package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun ModePresetsScreen(nav: NavController, vm: OnboardingViewModel) {
    var date by remember { mutableStateOf(true) }
    var workout by remember { mutableStateOf(false) }
    Scaffold(topBar = { TopAppBar(title = { Text("Mode Presets") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Row { Checkbox(date, { date = it }); Text("Date Mode") }
            Row { Checkbox(workout, { workout = it }); Text("Workout Mode") }
            Button(onClick = { vm.markStep(Routes.BATTERY_BG); nav.navigate(Routes.BATTERY_BG) }) { Text("Continue") }
        }
    }
}
