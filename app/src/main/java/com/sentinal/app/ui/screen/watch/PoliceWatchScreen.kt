package com.sentinal.app.ui.screen.watch

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import com.sentinal.app.util.Evidence
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun PoliceWatchScreen(nav: NavController) {
    val ctx = LocalContext.current
    val scope = rememberCoroutineScope()

    var mediaUri by remember { mutableStateOf<Uri?>(null) }
    var evidenceJsonUri by remember { mutableStateOf<Uri?>(null) }
    var busy by remember { mutableStateOf(false) }
    var lastHash by remember { mutableStateOf<String?>(null) }

    val captureVideo = rememberLauncherForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { res ->
        val dataUri = res.data?.data
        if (res.resultCode == Activity.RESULT_OK && dataUri != null) {
            mediaUri = dataUri
            busy = true
            scope.launch {
                val result = withContext(Dispatchers.IO) {
                    Evidence.generate(ctx, dataUri, eventType = "Police Interaction", notes = "")
                }
                evidenceJsonUri = result.jsonUri
                lastHash = result.mediaSha256
                busy = false
                Toast.makeText(ctx, "Evidence ready", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(ctx, "Recording cancelled", Toast.LENGTH_SHORT).show()
        }
    }

    fun startRecording() {
        val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_DURATION_LIMIT, 600) // 10 min
            putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
        }
        captureVideo.launch(intent)
    }

    fun shareEvidence() {
        val u1 = mediaUri ?: return
        val u2 = evidenceJsonUri ?: return
        val send = Intent(Intent.ACTION_SEND_MULTIPLE).apply {
            type = "*/*"
            putParcelableArrayListExtra(Intent.EXTRA_STREAM, arrayListOf(u1, u2))
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        ctx.startActivity(Intent.createChooser(send, "Share Evidence…"))
    }

    val scroll = rememberScrollState()

    Scaffold(topBar = { TopAppBar(title = { Text("Police Watch Mode") }) }) { pad ->
        Column(
            modifier = Modifier
                .padding(pad)
                .navigationBarsPadding()
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "This mode records via the system camera app. After saving, Sentinal creates an evidence file with a hash and metadata.",
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                enabled = !busy,
                onClick = { startRecording() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) { Text("Start Recording") }

            if (mediaUri != null) {
                ElevatedCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("Evidence Ready", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold))
                        Text("Video: $mediaUri")
                        Text("Sidecar JSON: ${evidenceJsonUri ?: "—"}")
                        Text("SHA-256: ${lastHash ?: "—"}", maxLines = 2)
                        Spacer(Modifier.height(8.dp))
                        Button(
                            enabled = (mediaUri != null && evidenceJsonUri != null && !busy),
                            onClick = { shareEvidence() }
                        ) { Text("Share Evidence") }
                    }
                }
            }

            if (busy) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                Text("Preparing evidence…")
            }

            Spacer(Modifier.height(24.dp))
        }
    }
}
