package com.sentinal.app.geofence

import android.content.Context
import com.sentinal.app.ui.screen.trusted.TrustedPlace

/**
 * Reads/writes trusted places from SharedPreferences CSV:
 * One place per line: name,lat,lng,radiusMeters
 */
object TrustedPlacesStore {
    private const val PREFS = "profile_prefs"
    private const val KEY = "trusted_places_csv"

    fun load(ctx: Context): List<TrustedPlace> {
        val sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val raw = sp.getString(KEY, "") ?: ""
        if (raw.isBlank()) return emptyList()
        val out = mutableListOf<TrustedPlace>()
        raw.lineSequence().forEach { line ->
            val t = line.trim()
            if (t.isEmpty()) return@forEach
            val parts = t.split(",")
            if (parts.size >= 4) {
                val name = parts[0].trim()
                val lat = parts[1].toDoubleOrNull()
                val lng = parts[2].toDoubleOrNull()
                val r   = parts[3].toFloatOrNull()
                if (!name.isBlank() && lat != null && lng != null && r != null && r > 0f) {
                    out += TrustedPlace(name, lat, lng, r)
                }
            }
        }
        return out
    }
}
