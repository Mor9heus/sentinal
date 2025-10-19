package com.sentinal.app.geofence

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingEvent
import com.sentinal.app.util.ProfilePrefs
import com.sentinal.app.util.WatchMode

/**
 * Reacts to ENTER/EXIT:
 * - On EXIT: if Auto-arm ON and watch not active, start watch (Other mode, default sensitivity).
 * - On ENTER: if watch is active and we auto-armed it, stop.
 */
class GeofenceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val event = GeofencingEvent.fromIntent(intent) ?: return
        if (event.hasError()) return

        val transition = event.geofenceTransition
        val autoArmOn = ProfilePrefs.isAutoArm(context)

        when (transition) {
            Geofence.GEOFENCE_TRANSITION_EXIT -> {
                if (autoArmOn && !WatchMode.isActive(context)) {
                    val mode = WatchMode.Mode.OTHER
                    val sens = WatchMode.defaultSensitivity(mode)
                    AutoArmState.setAutoArmed(context, true)
                    WatchMode.start(context, mode, sens)
                    Log.d("Sentinal", "Auto-armed Watch Mode (EXIT trusted area)")
                }
            }
            Geofence.GEOFENCE_TRANSITION_ENTER -> {
                if (AutoArmState.wasAutoArmed(context) && WatchMode.isActive(context)) {
                    WatchMode.stop(context)
                    AutoArmState.setAutoArmed(context, false)
                    Log.d("Sentinal", "Stopped auto-armed Watch Mode (ENTER trusted area)")
                }
            }
        }
    }
}

private object AutoArmState {
    private const val PREFS = "profile_prefs"
    private const val KEY = "auto_watch_active"

    fun setAutoArmed(ctx: Context, v: Boolean) {
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putBoolean(KEY, v).apply()
    }

    fun wasAutoArmed(ctx: Context): Boolean =
        ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getBoolean(KEY, false)
}
