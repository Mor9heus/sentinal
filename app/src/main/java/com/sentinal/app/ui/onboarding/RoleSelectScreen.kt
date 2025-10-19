package com.sentinal.app.ui.onboarding

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun RoleSelectScreen(nav: NavController, vm: OnboardingViewModel) {
    var role by remember { mutableStateOf("ADULT") }
    Scaffold(topBar = { TopAppBar(title = { Text("Role Selection") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            RoleRadio("ADULT", role) { role = it }
            RoleRadio("TEEN", role) { role = it }
            RoleRadio("CHILD", role) { role = it }
            RoleRadio("ELDERLY", role) { role = it }
            RoleRadio("CAREGIVER", role) { role = it }
            Button(onClick = {
                vm.saveRole(role)
                vm.markStep(Routes.PERMISSIONS)
                nav.navigate(Routes.PERMISSIONS)
            }) { Text("Continue") }
        }
    }
}

@Composable private fun RoleRadio(value: String, selected: String, onSel: (String) -> Unit) {
    Row { RadioButton(selected = value == selected, onClick = { onSel(value) }); Text(value) }
}
