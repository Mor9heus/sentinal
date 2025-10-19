package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun EncryptionScreen(nav: NavController, vm: OnboardingViewModel) {
    Scaffold(topBar = { TopAppBar(title = { Text("Encryption & Vault") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Text("Keys will be generated on first real use.")
            Button(onClick = { vm.markStep(Routes.QUICK_TEST); nav.navigate(Routes.QUICK_TEST) }) { Text("Continue") }
        }
    }
}
