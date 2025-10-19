package com.sentinal.app.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sentinal.app.geofence.GeofenceRegistrar
import com.sentinal.app.util.NotificationChannels
import com.sentinal.app.util.WatchMode

class GuardianService : Service() {

    override fun onCreate() {
        super.onCreate()
        NotificationChannels.ensure(this)
        startForeground(NOTIF_ID, baseNotification())

        // Ensure geofences are registered when the guard starts
        GeofenceRegistrar.registerAll(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        updateNotification()
        // (Sensor stubs could start here as well)
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun baseNotification() = NotificationCompat.Builder(this, NotificationChannels.CH_GUARDIAN)
        .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
        .setContentTitle("Sentinal protection running")
        .setContentText("Impact & Water safety active; watch mode off")
        .setOngoing(true)
        .build()

    private fun updateNotification() {
        val active = WatchMode.isActive(this)
        val mode = WatchMode.currentMode(this).name.lowercase().replaceFirstChar { it.uppercase() }
        val sens = WatchMode.sensitivity(this)

        val text = if (active) "Watch: $mode • Sens $sens • Impact & Water active"
                   else "Impact & Water safety active • Watch off"

        val notif = NotificationCompat.Builder(this, NotificationChannels.CH_GUARDIAN)
            .setSmallIcon(android.R.drawable.ic_lock_silent_mode_off)
            .setContentTitle("Sentinal protection running")
            .setContentText(text)
            .setOngoing(true)
            .build()

        NotificationManagerCompat.from(this).notify(NOTIF_ID, notif)
    }

    companion object {
        private const val NOTIF_ID = 101

        fun start(ctx: Context) {
            val i = Intent(ctx, GuardianService::class.java)
            ctx.startForegroundService(i)
        }

        fun stop(ctx: Context) {
            ctx.stopService(Intent(ctx, GuardianService::class.java))
        }
    }
}
