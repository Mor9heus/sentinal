package com.sentinal.app.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object NotificationChannels {
    const val CH_GUARDIAN = "guardian"
    const val CH_CURFEW = "curfew"

    fun ensure(ctx: Context) {
        if (Build.VERSION.SDK_INT < 26) return
        val nm = ctx.getSystemService(NotificationManager::class.java)

        if (nm.getNotificationChannel(CH_GUARDIAN) == null) {
            nm.createNotificationChannel(
                NotificationChannel(
                    CH_GUARDIAN,
                    "Guardian Protection",
                    NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Background protection & quick actions"
                    setShowBadge(false)
                }
            )
        }

        if (nm.getNotificationChannel(CH_CURFEW) == null) {
            nm.createNotificationChannel(
                NotificationChannel(
                    CH_CURFEW,
                    "Curfew Reminders",
                    NotificationManager.IMPORTANCE_DEFAULT
                ).apply {
                    description = "Curfew heads-up 30 minutes before time"
                    setShowBadge(true)
                }
            )
        }
    }
}
