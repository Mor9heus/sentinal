package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun FinalReviewScreen(nav: NavController, vm: OnboardingViewModel) {
    Scaffold(topBar = { TopAppBar(title = { Text("Review & Finish") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Summary looks good. Geofence auto‑arm for dependents; caregivers inherit rules.")
            Button(onClick = {
                vm.setProfileComplete()
                when (vm.roleHomeRoute()) {
                    Routes.HOME_CHILD -> nav.navigate(Routes.HOME_CHILD) { popUpTo(0) }
                    Routes.HOME_ELDER -> nav.navigate(Routes.HOME_ELDER) { popUpTo(0) }
                    Routes.HOME_CAREGIVER -> nav.navigate(Routes.HOME_CAREGIVER) { popUpTo(0) }
                    else -> nav.navigate(Routes.HOME_ADULT) { popUpTo(0) }
                }
            }) { Text("Finish Setup") }
        }
    }
}
