package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun QuickTestScreen(nav: NavController, vm: OnboardingViewModel) {
    Scaffold(topBar = { TopAppBar(title = { Text("Quick Safety Test") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Text("Dry-run SOS, Guardian demo, and capture checks will be added.")
            Button(onClick = { vm.markStep(Routes.FINAL_REVIEW); nav.navigate(Routes.FINAL_REVIEW) }) { Text("Continue") }
        }
    }
}
