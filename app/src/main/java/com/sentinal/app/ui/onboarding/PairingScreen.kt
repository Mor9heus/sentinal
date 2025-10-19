package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun PairingScreen(nav: NavController, vm: OnboardingViewModel) {
    Scaffold(topBar = { TopAppBar(title = { Text("Device Pairing (Home Wi‑Fi)") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Auto-scan is limited to your saved Home Wi‑Fi. You can also link later.")
            Button(onClick = { vm.markStep(Routes.CONTACTS_ROLES); nav.navigate(Routes.CONTACTS_ROLES) }) { Text("Continue") }
        }
    }
}
