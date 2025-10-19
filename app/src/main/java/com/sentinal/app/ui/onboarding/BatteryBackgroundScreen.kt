package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun BatteryBackgroundScreen(nav: NavController, vm: OnboardingViewModel) {
    Scaffold(topBar = { TopAppBar(title = { Text("Background Readiness") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Text("We’ll guide you to battery optimization settings later.")
            Button(onClick = { vm.markStep(Routes.ENCRYPTION); nav.navigate(Routes.ENCRYPTION) }) { Text("Continue") }
        }
    }
}
