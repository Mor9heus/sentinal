package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun WelcomeScreen(nav: NavController) {
    Scaffold(topBar = { TopAppBar(title = { Text("Welcome") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Text("Sentinal — Affordable Safety.")
            Spacer(Modifier.height(16.dp))
            Button(onClick = { nav.navigate(Routes.ROLE) }) { Text("Get Started") }
        }
    }
}
