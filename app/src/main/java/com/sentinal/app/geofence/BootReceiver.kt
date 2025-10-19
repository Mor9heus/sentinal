package com.sentinal.app.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

/**
 * Re-register geofences after reboot or app update.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        GeofenceRegistrar.registerAll(context)
    }
}
