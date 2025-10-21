package com.sentinal.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.sentinal.app.util.NotificationUtils
import com.sentinal.app.util.ServiceActions
import com.sentinal.app.util.VoiceSession

class VoiceAgentService : Service() {

    private var session: VoiceSession? = null
    private val notifId = 2024

    override fun onCreate() {
        super.onCreate()
        NotificationUtils.ensureChannels(this)
        session = VoiceSession(this).also { it.init() }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ServiceActions.ACTION_STOP_WATCH -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                startForeground(
                    notifId,
                    NotificationCompat.Builder(this, NotificationUtils.CH_WATCH_STATUS)
                        .setSmallIcon(applicationInfo.icon.takeIf { it != 0 } ?: android.R.drawable.ic_dialog_info)
                        .setOngoing(true)
                        .setContentTitle("Emergency Auto-Assist")
                        .setContentText("Listening and ready to respond")
                        .build()
                )
                startContinuousAssist()
            }
        }
        return START_STICKY
    }

    private fun startContinuousAssist() {
        val s = session ?: return
        // Simple keyword routing; expand as needed
        s.startListening(
            onResult = { text ->
                val lower = text.lowercase()
                when {
                    // Dispatcher asks: "What is your emergency?"
                    listOf("what is your emergency", "describe emergency", "what happened").any { lower.contains(it) } ->
                        s.speak("Police. I am in danger. Please send help.")

                    // Dispatcher: "What is your address / location?"
                    listOf("address", "where are you", "location").any { lower.contains(it) } ->
                        s.speak("My location is unknown. Please use the phone's GPS. I will try to provide landmarks.")

                    // "Are there injuries?"
                    listOf("injur", "hurt", "bleeding", "medical").any { lower.contains(it) } ->
                        s.speak("Yes. There may be injuries. Please send an ambulance as well.")

                    // "Is the suspect there?"
                    listOf("suspect", "attacker", "intruder").any { lower.contains(it) } ->
                        s.speak("I do not know. Please advise if I should remain silent.")

                    // fallback: minimal acknowledgement to keep line active
                    else -> {
                        // keep quiet unless clearly asked — prevents chatter
                    }
                }
            },
            onError = { /* could post a small heads-up in notification if you want */ }
        )
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        session?.release()
        session = null
        super.onDestroy()
    }

    companion object {
        fun start(context: android.content.Context) {
            val i = Intent(context, VoiceAgentService::class.java)
            ContextCompat.startForegroundService(context, i)
        }

        fun stop(context: android.content.Context) {
            val i = Intent(context, VoiceAgentService::class.java).apply {
                action = ServiceActions.ACTION_STOP_WATCH
            }
            ContextCompat.startForegroundService(context, i)
        }
    }
}
