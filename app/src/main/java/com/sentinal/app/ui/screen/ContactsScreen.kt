package com.sentinal.app.ui.screen

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ContactsScreen(nav: NavController) {
    Scaffold(topBar = { TopAppBar(title = { Text("Contacts") }) }) { pad ->
        Column(Modifier.padding(pad).padding(16.dp)) {
            Text("Add/edit contacts later — stub.")
        }
    }
}
