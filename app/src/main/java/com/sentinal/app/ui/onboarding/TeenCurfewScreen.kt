package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun TeenCurfewScreen(nav: NavController, vm: OnboardingViewModel) {
    Scaffold(topBar = { TopAppBar(title = { Text("Teen Curfew") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Text("Parents can set or disable curfew later.")
            Button(onClick = { vm.markStep(Routes.ATTORNEY); nav.navigate(Routes.ATTORNEY) }) { Text("Continue") }
        }
    }
}
