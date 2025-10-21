package com.sentinal.app.util

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build

/**
 * Safely start services across API levels.
 * - On API 26+ uses startForegroundService
 * - Else uses startService
 */
object ServiceCompat {

    /**
     * Use when the Service will call startForeground() promptly.
     */
    fun startForeground(
        context: Context,
        intent: Intent
    ) {
        if (Build.VERSION.SDK_INT >= 26) {
            context.startForegroundService(intent)
        } else {
            context.startService(intent)
        }
    }

    /**
     * Simple start when you don't need a foreground service.
     */
    fun start(context: Context, intent: Intent) {
        context.startService(intent)
    }
}

/**
 * Convenience extension.
 */
fun Context.startGuardianService(serviceClass: Class<out Service>) {
    ServiceCompat.startForeground(this, Intent(this, serviceClass))
}
