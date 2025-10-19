package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun ContactsRolesScreen(nav: NavController, vm: OnboardingViewModel) {
    Scaffold(topBar = { TopAppBar(title = { Text("Contacts & Roles") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Add contacts in Settings ▸ Contacts anytime.")
            Button(onClick = {
                val next = if (vm.getRole() == "TEEN") Routes.TEEN_CURFEW else Routes.ATTORNEY
                vm.markStep(next)
                nav.navigate(next)
            }) { Text("Continue") }
        }
    }
}
