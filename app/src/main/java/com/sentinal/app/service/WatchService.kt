package com.sentinal.app.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.content.ContextCompat
import com.sentinal.app.util.NotificationUtils
import com.sentinal.app.util.ServiceActions

class WatchService : Service() {

    override fun onCreate() {
        super.onCreate()
        // Create channels on boot (safe multi-call).
        NotificationUtils.ensureChannels(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ServiceActions.ACTION_STOP_WATCH -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
            else -> {
                val notif = NotificationUtils.buildWatchStatusNotification(this)
                startForeground(101, notif)
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        /** Convenience starters you can call from any Context. */
        fun start(context: android.content.Context) {
            val i = Intent(context, WatchService::class.java).apply {
                action = ServiceActions.ACTION_START_WATCH
            }
            ContextCompat.startForegroundService(context, i)
        }
        fun stop(context: android.content.Context) {
            val i = Intent(context, WatchService::class.java).apply {
                action = ServiceActions.ACTION_STOP_WATCH
            }
            ContextCompat.startForegroundService(context, i)
        }
    }
}
