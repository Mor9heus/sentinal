package com.sentinal.app.util

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.sentinal.app.MainActivity
import com.sentinal.app.service.WatchService

// Actions for the Stop button
import com.sentinal.app.util.ServiceActions

object NotificationUtils {
    const val CH_WATCH_STATUS = "watch_status"
    const val CH_ALERTS = "alerts"

    /** Safe to call multiple times. No-op below API 26. */
    fun ensureChannels(ctx: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val nm = ctx.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val watch = NotificationChannel(
            CH_WATCH_STATUS,
            "Watch Status",
            NotificationManager.IMPORTANCE_LOW
        ).apply { description = "Ongoing status for Watch Mode" }

        val alerts = NotificationChannel(
            CH_ALERTS,
            "Alerts",
            NotificationManager.IMPORTANCE_HIGH
        ).apply { description = "Urgent alerts and warnings" }

        nm.createNotificationChannel(watch)
        nm.createNotificationChannel(alerts)
    }

    /** Ongoing foreground notification for Watch Mode (with Stop action). */
    fun buildWatchStatusNotification(
        context: Context,
        text: String = "Watch Mode active",
        addStopAction: Boolean = true
    ): Notification {
        val smallIcon = context.applicationInfo.icon
            .takeIf { it != 0 } ?: android.R.drawable.ic_dialog_info

        val contentPi = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java),
            PendingIntent.FLAG_IMMUTABLE
        )

        val builder = NotificationCompat.Builder(context, CH_WATCH_STATUS)
            .setSmallIcon(smallIcon)
            .setContentTitle("Watch Mode")
            .setContentText(text)
            .setContentIntent(contentPi)
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)

        if (addStopAction) {
            val stopIntent = Intent(context, WatchService::class.java)
                .apply { action = ServiceActions.ACTION_STOP_WATCH }
            val stopPi = PendingIntent.getService(
                context, 1, stopIntent, PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(0, "Stop", stopPi)
        }

        return builder.build()
    }
}
