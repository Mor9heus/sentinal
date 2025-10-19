package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun SplashScreen(nav: NavController, vm: OnboardingViewModel) {
    LaunchedEffect(Unit) {
        val next = vm.currentStep() ?: Routes.WELCOME
        nav.navigate(next) { popUpTo(Routes.SPLASH) { inclusive = true } }
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Sentinal", style = MaterialTheme.typography.headlineMedium)
            Spacer(Modifier.height(8.dp))
            Text("Affordable Safety")
        }
    }
}
