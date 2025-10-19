package com.sentinal.app.ui.screen.watch

import android.content.Context
import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.sentinal.app.util.WatchMode
import java.io.File
import java.io.FileOutputStream

@Composable
fun WatchModeScreen(nav: NavController) {
    val ctx = LocalContext.current

    var active by remember { mutableStateOf(WatchMode.isActive(ctx)) }
    var mode by remember { mutableStateOf(WatchMode.currentMode(ctx)) }
    var sensitivity by remember { mutableStateOf(WatchMode.sensitivity(ctx)) }

    var dateName by remember { mutableStateOf(WatchMode.getDateName(ctx)) }
    var datePhone by remember { mutableStateOf(WatchMode.getDatePhone(ctx)) }
    var dateWhere by remember { mutableStateOf(WatchMode.getDateWhere(ctx)) }
    var photoPath by remember { mutableStateOf(WatchMode.getDatePhoto(ctx)) }

    val takePhoto = rememberLauncherForActivityResult(ActivityResultContracts.TakePicturePreview()) { bmp: Bitmap? ->
        bmp?.let { photoPath = saveBitmap(ctx, it, "date_photo_${System.currentTimeMillis()}.jpg") }
    }

    val scroll = rememberScrollState()

    Scaffold(topBar = { TopAppBar(title = { Text("Watch Mode") }) }) { pad ->
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
            Text("Pick an activity mode, then Start Watch. SOS remains separate and can be used anytime.", style = MaterialTheme.typography.bodyMedium)

            FlowRow {
                ModeChip("Jogging", WatchMode.Mode.JOGGING, mode) { mode = it; sensitivity = WatchMode.defaultSensitivity(it) }
                ModeChip("Workout", WatchMode.Mode.WORKOUT, mode) { mode = it; sensitivity = WatchMode.defaultSensitivity(it) }
                ModeChip("Concert/Crowded", WatchMode.Mode.CONCERT, mode) { mode = it; sensitivity = WatchMode.defaultSensitivity(it) }
                ModeChip("Date", WatchMode.Mode.DATE, mode) { mode = it; sensitivity = WatchMode.defaultSensitivity(it) }
                ModeChip("Police", WatchMode.Mode.POLICE, mode) { mode = it; sensitivity = WatchMode.defaultSensitivity(it) }
                ModeChip("Church", WatchMode.Mode.CHURCH, mode) { mode = it; sensitivity = WatchMode.defaultSensitivity(it) }
                ModeChip("Other", WatchMode.Mode.OTHER, mode) { mode = it; sensitivity = WatchMode.defaultSensitivity(it) }
            }

            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Aggression Sensitivity: $sensitivity")
                Slider(value = sensitivity.toFloat(), onValueChange = { sensitivity = it.toInt() }, steps = 100, valueRange = 0f..100f)
                Text("Tip: Jogging/Workout lower avoids false alarms; Concert/Crowded higher boosts vigilance.")
            }

            if (mode == WatchMode.Mode.DATE) {
                HorizontalDivider()
                Text("Date Details", style = MaterialTheme.typography.titleMedium)
                OutlinedTextField(dateName, { dateName = it }, label = { Text("Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(datePhone, { datePhone = it }, label = { Text("Phone") }, singleLine = true,
                    modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone))
                OutlinedTextField(dateWhere, { dateWhere = it }, label = { Text("Where you met / know each other") }, modifier = Modifier.fillMaxWidth())
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Button(onClick = { takePhoto.launch(null) }) { Text(if (photoPath.isNullOrBlank()) "Add Photo" else "Retake Photo") }
                    if (!photoPath.isNullOrBlank()) Text("Photo ✓")
                }
            }

            HorizontalDivider()

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(
                    enabled = !active,
                    onClick = {
                        if (mode == WatchMode.Mode.DATE) {
                            WatchMode.setDateDetails(ctx, dateName.trim(), datePhone.trim(), dateWhere.trim(), photoPath)
                        }
                        WatchMode.start(ctx, mode, sensitivity)
                        active = true
                    }
                ) { Text("Start Watch") }

                OutlinedButton(
                    enabled = active,
                    onClick = {
                        WatchMode.stop(ctx)
                        active = false
                    }
                ) { Text("Stop Watch") }
            }

            Text(
                if (active) "Watch Mode Active: ${mode.name} (sens $sensitivity)"
                else "Watch Mode is OFF. Impact & Water safety remain active.",
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable
private fun FlowRow(content: @Composable RowScope.() -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) { content() }
    }
}

@Composable
private fun ModeChip(label: String, value: WatchMode.Mode, selected: WatchMode.Mode, onPick: (WatchMode.Mode) -> Unit) {
    FilterChip(selected = selected == value, onClick = { onPick(value) }, label = { Text(label) })
}

private fun saveBitmap(ctx: Context, bmp: Bitmap, filename: String): String {
    val file = File(ctx.filesDir, filename)
    FileOutputStream(file).use { out ->
        bmp.compress(Bitmap.CompressFormat.JPEG, 92, out)
        out.flush()
    }
    return file.absolutePath
}
