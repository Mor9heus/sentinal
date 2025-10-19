package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun PermissionsPrimerScreen(nav: NavController, vm: OnboardingViewModel) {
    Scaffold(topBar = { TopAppBar(title = { Text("Permissions Primer") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("We’ll ask for mic, camera, location, Bluetooth and notifications when needed.")
            Text("Sentinal does not record calls; it detects emergencies via patterns.")
            Button(onClick = {
                vm.setPermissionsSeen()
                vm.markStep(Routes.CONSENT)
                nav.navigate(Routes.CONSENT)
            }) { Text("Continue") }
        }
    }
}
