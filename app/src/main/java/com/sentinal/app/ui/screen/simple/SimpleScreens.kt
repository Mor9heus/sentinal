package com.sentinal.app.ui.screen.simple

import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.sentinal.app.ui.Routes
import com.sentinal.app.util.ProfilePrefs
import com.sentinal.app.util.SosIntent
import com.sentinal.app.util.WatchMode

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AdultHomeScreen(nav: NavController) {
    val ctx = LocalContext.current

    var c1 by remember { mutableStateOf(ProfilePrefs.getContact1Number(ctx)) }
    var c2 by remember { mutableStateOf(ProfilePrefs.getContact2Number(ctx)) }
    var c1Name by remember { mutableStateOf(ProfilePrefs.getContact1Name(ctx)) }
    var c2Name by remember { mutableStateOf(ProfilePrefs.getContact2Name(ctx)) }

    var pendingVideoUri by remember { mutableStateOf<Uri?>(null) }
    val captureVideo = rememberLauncherForActivityResult(ActivityResultContracts.CaptureVideo()) { }
    val audioLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { }

    fun launchVideoCapture() {
        val values = ContentValues().apply {
            put(MediaStore.Video.Media.DISPLAY_NAME, "sentinal_${System.currentTimeMillis()}.mp4")
            put(MediaStore.Video.Media.MIME_TYPE, "video/mp4")
        }
        ctx.contentResolver.insert(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, values)?.let { uri ->
            pendingVideoUri = uri
            captureVideo.launch(uri)
        } ?: Toast.makeText(ctx, "Unable to create video file", Toast.LENGTH_LONG).show()
    }

    fun launchAudioRecord() {
        val toLaunch = Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION)
        if (toLaunch.resolveActivity(ctx.packageManager) != null) {
            audioLauncher.launch(toLaunch)
        } else {
            Toast.makeText(ctx, "No audio recorder app found.", Toast.LENGTH_LONG).show()
        }
    }

    fun sendSosSms() {
        val body = "SOS — Sentinal needs help. My location will be shared from the app."
        SosIntent.sendSmsToContacts(ctx, listOf(c1, c2), body)
        Toast.makeText(ctx, "Opening SMS to contacts…", Toast.LENGTH_SHORT).show()
    }

    val watchActive by remember { mutableStateOf(WatchMode.isActive(ctx)) }
    val watchStatusText = remember(watchActive) {
        if (watchActive) {
            val mode = WatchMode.currentMode(ctx).name.lowercase().replaceFirstChar { it.uppercase() }
            "Watch: $mode (sens ${WatchMode.sensitivity(ctx)})"
        } else "Watch off"
    }

    val scroll = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Sentinal — Adult") },
                actions = {
                    TextButton(onClick = { nav.navigate(Routes.WATCH_MODE) }) { Text("Watch Mode") }
                    TextButton(onClick = { nav.navigate(Routes.SETTINGS) }) { Text("Settings") }
                }
            )
        }
    ) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .navigationBarsPadding()
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("SOS", style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold))
                    Spacer(Modifier.height(8.dp))
                    Text("Tap: Text your contacts • Long-press: Dial 911")

                    Spacer(Modifier.height(16.dp))
                    Button(
                        onClick = { sendSosSms() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .combinedClickable(onClick = { sendSosSms() }, onLongClick = { SosIntent.dial911(ctx) }),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                    ) { Text("SEND SOS", color = MaterialTheme.colorScheme.onError) }

                    Spacer(Modifier.height(12.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        AssistChip(onClick = { nav.navigate(Routes.SETTINGS) }, label = { Text(if (c1Name.isBlank()) "Contact #1" else c1Name) })
                        AssistChip(onClick = { nav.navigate(Routes.SETTINGS) }, label = { Text(if (c2Name.isBlank()) "Contact #2" else c2Name) })
                    }
                }
            }

            ElevatedCard(modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Watch Mode", style = MaterialTheme.typography.titleMedium)
                    Text(watchStatusText)
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Button(onClick = { nav.navigate(Routes.WATCH_MODE) }, modifier = Modifier.weight(1f)) { Text("Open Watch Mode") }
                        OutlinedButton(onClick = { nav.navigate(Routes.POLICE_WATCH) }, modifier = Modifier.weight(1f)) { Text("Police Watch") }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Pill("Video") { launchVideoCapture() }
                Pill("Audio") { launchAudioRecord() }
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}

@Composable fun ChildHomeScreen(nav: NavController) { SimpleRoleHome("Sentinal — Child", nav) }
@Composable fun CaregiverHomeScreen(nav: NavController) { SimpleRoleHome("Sentinal — Caregiver", nav) }
@Composable fun ElderHomeScreen(nav: NavController) { SimpleRoleHome("Sentinal — Elder", nav) }

@Composable
private fun SimpleRoleHome(title: String, nav: NavController) {
    val scroll = rememberScrollState()
    Scaffold(topBar = {
        TopAppBar(title = { Text(title) }, actions = {
            TextButton(onClick = { nav.navigate(Routes.WATCH_MODE) }) { Text("Watch Mode") }
            TextButton(onClick = { nav.navigate(Routes.SETTINGS) }) { Text("Settings") }
        })
    }) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .navigationBarsPadding()
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) { Text("Home (stub)") }
    }
}

@Composable
private fun Pill(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        modifier = Modifier.widthIn(min = 140.dp).height(40.dp),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 6.dp)
    ) { Text(text) }
}
