package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun ConsentTutorialScreen(nav: NavController, vm: OnboardingViewModel) {
    var a by remember { mutableStateOf(false) }
    var b by remember { mutableStateOf(false) }
    Scaffold(topBar = { TopAppBar(title = { Text("Consent & Tutorial") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row { Checkbox(a, { a = it }); Spacer(Modifier.width(8.dp)); Text("Sentinal assists; not a guarantee.") }
            Row { Checkbox(b, { b = it }); Spacer(Modifier.width(8.dp)); Text("Local encryption; you control sharing.") }
            Button(enabled = a && b, onClick = {
                vm.setConsentAccepted()
                vm.markStep(Routes.SAFETY_PROFILE)
                nav.navigate(Routes.SAFETY_PROFILE)
            }) { Text("Agree & Continue") }
        }
    }
}
