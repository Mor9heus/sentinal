package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun AttorneyScreen(nav: NavController, vm: OnboardingViewModel) {
    var name by remember { mutableStateOf("") }
    var sms by remember { mutableStateOf("") }
    Scaffold(topBar = { TopAppBar(title = { Text("Attorney Contact (Optional)") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(name, { name = it }, label = { Text("Name") })
            OutlinedTextField(sms, { sms = it }, label = { Text("SMS number") })
            Button(onClick = { vm.markStep(Routes.MODE_PRESETS); nav.navigate(Routes.MODE_PRESETS) }) { Text("Continue") }
        }
    }
}
