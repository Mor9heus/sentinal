package com.sentinal.app.ui.screen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.sentinal.app.service.VoiceAgentService
import com.sentinal.app.util.NotificationUtils
import com.sentinal.app.util.ServiceActions

class EmergencyActivity : ComponentActivity() {

    private val reqMicPerm = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { /* no-op */ }

    private val reqNotiPerm = if (Build.VERSION.SDK_INT >= 33)
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }
    else null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NotificationUtils.ensureChannels(this)

        setContent {
            MaterialTheme {
                Surface(Modifier.fillMaxSize()) {
                    EmergencyScreen(
                        onRequestMic = {
                            reqMicPerm.launch(Manifest.permission.RECORD_AUDIO)
                        },
                        onRequestNoti = {
                            if (Build.VERSION.SDK_INT >= 33) {
                                reqNotiPerm?.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmergencyScreen(
    onRequestMic: () -> Unit,
    onRequestNoti: () -> Unit
) {
    val ctx = LocalContext.current

    Column(
        Modifier
            .padding(16.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Emergency", style = MaterialTheme.typography.headlineMedium)
        Text(
            "Use the options below. Auto-Assist listens and speaks for you with predefined responses.",
            style = MaterialTheme.typography.bodyMedium
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val i = Intent(Intent.ACTION_DIAL, Uri.parse("tel:911"))
                ctx.startActivity(i)
            }
        ) { Text("Call 911") }

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                val uri = Uri.parse("smsto:911")
                val i = Intent(Intent.ACTION_SENDTO, uri).apply {
                    putExtra("sms_body", "SOS — I need help. This is Sentinal.")
                }
                ctx.startActivity(i)
            }
        ) { Text("Text 911") }

        HorizontalDivider() // ← was Divider()

        Text("Auto-Assist", style = MaterialTheme.typography.titleLarge)
        Text(
            "Auto-Assist will use your mic to hear prompts and reply with preset phrases " +
                    "(e.g., \"Police\", \"My address is ...\", \"There are injuries\").",
            style = MaterialTheme.typography.bodySmall
        )

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Button(
                modifier = Modifier.weight(1f),
                onClick = {
                    onRequestNoti()
                    onRequestMic()
                    val i = Intent(ctx, VoiceAgentService::class.java)
                        .setAction(ServiceActions.ACTION_START_WATCH)
                    androidx.core.content.ContextCompat.startForegroundService(ctx, i)
                }
            ) { Text("Start Auto-Assist") }

            OutlinedButton(
                modifier = Modifier.weight(1f),
                onClick = {
                    val i = Intent(ctx, VoiceAgentService::class.java)
                        .setAction(ServiceActions.ACTION_STOP_WATCH)
                    androidx.core.content.ContextCompat.startForegroundService(ctx, i)
                }
            ) { Text("Stop Auto-Assist") }
        }
    }
}
