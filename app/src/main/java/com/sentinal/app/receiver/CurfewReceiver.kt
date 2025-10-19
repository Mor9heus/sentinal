package com.sentinal.app.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.sentinal.app.R
import com.sentinal.app.util.Curfew
import com.sentinal.app.util.NotificationChannels

class CurfewReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action != Curfew.ACTION_CURFEW_ALERT) return

        NotificationChannels.ensure(context) // make sure channels exist

        val notif = NotificationCompat.Builder(context, NotificationChannels.CH_CURFEW)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("Curfew in 30 minutes")
            .setContentText("Please head home or confirm an approved exception.")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        NotificationManagerCompat.from(context).notify(2001, notif)

        // Schedule the next one (next valid day)
        Curfew.scheduleNext(context)
    }
}
