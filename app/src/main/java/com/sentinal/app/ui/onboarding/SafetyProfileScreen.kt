package com.sentinal.app.ui.onboarding

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes

@Composable
fun SafetyProfileScreen(nav: NavController, vm: OnboardingViewModel) {
    val ctx = LocalContext.current
    val sp = remember { ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE) }
    var name by remember { mutableStateOf(sp.getString("profile_name", "") ?: "") }
    var dob by remember { mutableStateOf(sp.getString("profile_dob", "") ?: "") }
    var addr by remember { mutableStateOf(sp.getString("profile_home_address", "") ?: "") }

    Scaffold(topBar = { TopAppBar(title = { Text("Safety Profile") }) }) { pad ->
        Column(
            Modifier.padding(pad).padding(16.dp).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(name, { name = it }, label = { Text("Full name") }, singleLine = true)
            OutlinedTextField(dob, { dob = it }, label = { Text("DOB (YYYY-MM-DD)") }, singleLine = true)
            OutlinedTextField(addr, { addr = it }, label = { Text("Home address") })
            Button(onClick = {
                sp.edit().putString("profile_name", name)
                    .putString("profile_dob", dob)
                    .putString("profile_home_address", addr)
                    .apply()
                vm.markStep(Routes.PAIRING)
                nav.navigate(Routes.PAIRING)
            }) { Text("Save & Continue") }
        }
    }
}
