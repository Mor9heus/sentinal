package com.sentinal.app.ui.screen.trusted

import androidx.compose.material3.HorizontalDivider as Divider
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.util.ProfilePrefs

@Composable
fun TrustedPlacesScreen(nav: NavController) {
    val ctx = LocalContext.current

    // Load once; we’ll let user mutate in-memory then Save.
    var items by remember { mutableStateOf(ProfilePrefs.getTrustedPlaces(ctx)) }

    // New item inputs
    var newName by remember { mutableStateOf("") }
    var newLat by remember { mutableStateOf("") }
    var newLng by remember { mutableStateOf("") }
    var newRadius by remember { mutableStateOf("150") }

    val scroll = rememberScrollState()

    Scaffold(topBar = { TopAppBar(title = { Text("Trusted Places") }) }) { pad ->
        Column(
            Modifier
                .padding(pad)
                .navigationBarsPadding()
                .imePadding()
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "These are your “safe zones.” When Auto-arm is ON, leaving these areas can enable Watch Mode automatically for protected profiles.",
                style = MaterialTheme.typography.bodyMedium
            )

            // Existing places list
            if (items.isEmpty()) {
                Text("No trusted places yet.", style = MaterialTheme.typography.bodyMedium)
            } else {
                ElevatedCard {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        items.forEachIndexed { index, place ->
                            PlaceRow(
                                place = place,
                                onChange = { updated ->
                                    val copy = items.toMutableList()
                                    copy[index] = updated
                                    items = copy
                                },
                                onDelete = {
                                    val copy = items.toMutableList()
                                    copy.removeAt(index)
                                    items = copy
                                }
                            )
                            if (index < items.lastIndex) Divider()
                        }
                    }
                }
            }

            // Add new place
            Text("Add Place", style = MaterialTheme.typography.titleMedium)
            OutlinedTextField(newName, { newName = it }, label = { Text("Label (e.g., Home)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = newLat, onValueChange = { newLat = it },
                    label = { Text("Latitude") }, singleLine = true, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = newLng, onValueChange = { newLng = it },
                    label = { Text("Longitude") }, singleLine = true, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )
                OutlinedTextField(
                    value = newRadius, onValueChange = { newRadius = it },
                    label = { Text("Radius (m)") }, singleLine = true, modifier = Modifier.weight(1f),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = {
                    val name = newName.trim()
                    val lat = newLat.toDoubleOrNull()
                    val lng = newLng.toDoubleOrNull()
                    val radius = newRadius.toFloatOrNull()
                    if (name.isNotBlank() && lat != null && lng != null && radius != null && radius > 0) {
                        items = (items + TrustedPlace(name, lat, lng, radius)).toMutableList()
                        newName = ""; newLat = ""; newLng = ""; newRadius = "150"
                    }
                }) { Text("Add") }
                OutlinedButton(onClick = {
                    newName = ""; newLat = ""; newLng = ""; newRadius = "150"
                }) { Text("Clear") }
            }

            Divider()

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = {
                    ProfilePrefs.setTrustedPlaces(ctx, items)
                    nav.popBackStack()
                }) { Text("Save") }
                OutlinedButton(onClick = { items = ProfilePrefs.getTrustedPlaces(ctx) }) { Text("Revert") }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun PlaceRow(
    place: TrustedPlace,
    onChange: (TrustedPlace) -> Unit,
    onDelete: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(place.name, { onChange(place.copy(name = it)) }, label = { Text("Label") }, singleLine = true, modifier = Modifier.fillMaxWidth())
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
            OutlinedTextField(
                value = place.lat.toString(),
                onValueChange = { v -> v.toDoubleOrNull()?.let { onChange(place.copy(lat = it)) } },
                label = { Text("Latitude") }, singleLine = true, modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = place.lng.toString(),
                onValueChange = { v -> v.toDoubleOrNull()?.let { onChange(place.copy(lng = it)) } },
                label = { Text("Longitude") }, singleLine = true, modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )
            OutlinedTextField(
                value = place.radiusM.toString(),
                onValueChange = { v -> v.toFloatOrNull()?.let { onChange(place.copy(radiusM = it)) } },
                label = { Text("Radius (m)") }, singleLine = true, modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )
        }
        Row {
            OutlinedButton(onClick = onDelete) { Text("Remove") }
        }
    }
}
