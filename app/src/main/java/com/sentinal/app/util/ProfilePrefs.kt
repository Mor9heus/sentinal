package com.sentinal.app.util

import android.content.Context
import com.sentinal.app.ui.screen.trusted.TrustedPlace

/**
 * Tiny wrapper around SharedPreferences so UI code stays clean.
 * Backed by the same "profile_prefs" store you’ve been using.
 */
object ProfilePrefs {
    private const val PREFS = "profile_prefs"

    // Common keys
    private const val KEY_CONTACT1_NAME   = "contact1_name"
    private const val KEY_CONTACT1_NUMBER = "contact1_number"
    private const val KEY_CONTACT2_NAME   = "contact2_name"
    private const val KEY_CONTACT2_NUMBER = "contact2_number"

    private const val KEY_THEME = "ui_theme"                // "system" | "light" | "dark"
    private const val KEY_AUTO_ARM = "geofence_auto_arm"    // Boolean
    private const val KEY_ROLE = "profile_role"             // "ADULT" | "CHILD" | "ELDERLY" | "CAREGIVER"
    private const val KEY_PLACES = "trusted_places_csv"     // CSV of name|lat|lng|radius

    private fun sp(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // Contacts
    fun getContact1Name(ctx: Context)   = sp(ctx).getString(KEY_CONTACT1_NAME, "") ?: ""
    fun getContact1Number(ctx: Context) = sp(ctx).getString(KEY_CONTACT1_NUMBER, "") ?: ""
    fun getContact2Name(ctx: Context)   = sp(ctx).getString(KEY_CONTACT2_NAME, "") ?: ""
    fun getContact2Number(ctx: Context) = sp(ctx).getString(KEY_CONTACT2_NUMBER, "") ?: ""
    fun setContacts(ctx: Context, c1Name: String, c1Num: String, c2Name: String, c2Num: String) {
        sp(ctx).edit()
            .putString(KEY_CONTACT1_NAME, c1Name)
            .putString(KEY_CONTACT1_NUMBER, c1Num)
            .putString(KEY_CONTACT2_NAME, c2Name)
            .putString(KEY_CONTACT2_NUMBER, c2Num)
            .apply()
    }

    // Theme
    fun getTheme(ctx: Context) = sp(ctx).getString(KEY_THEME, "system") ?: "system"
    fun setTheme(ctx: Context, value: String) { sp(ctx).edit().putString(KEY_THEME, value).apply() }

    // Geofence auto-arm
    fun isAutoArm(ctx: Context) = sp(ctx).getBoolean(KEY_AUTO_ARM, true)
    fun setAutoArm(ctx: Context, enabled: Boolean) { sp(ctx).edit().putBoolean(KEY_AUTO_ARM, enabled).apply() }

    // Role (defaults to ADULT)
    fun getRole(ctx: Context) = sp(ctx).getString(KEY_ROLE, "ADULT") ?: "ADULT"
    fun setRole(ctx: Context, role: String) { sp(ctx).edit().putString(KEY_ROLE, role).apply() }

    // Trusted places
    fun getTrustedPlaces(ctx: Context): MutableList<TrustedPlace> {
        val raw = sp(ctx).getString(KEY_PLACES, "") ?: ""
        if (raw.isBlank()) return mutableListOf()
        return raw.split("\n").filter { it.isNotBlank() }.mapNotNull { line ->
            val parts = line.split("|")
            runCatching {
                TrustedPlace(parts[0], parts[1].toDouble(), parts[2].toDouble(), parts[3].toFloat())
            }.getOrNull()
        }.toMutableList()
    }
    fun setTrustedPlaces(ctx: Context, items: List<TrustedPlace>) {
        val csv = items.joinToString("\n") { "${it.name}|${it.lat}|${it.lng}|${it.radiusM}" }
        sp(ctx).edit().putString(KEY_PLACES, csv).apply()
    }

    // Curfew passthrough (uses keys from Curfew.kt)
    fun isCurfewEnabled(ctx: Context) = sp(ctx).getBoolean(Curfew.KEY_ENABLED, false)
    fun setCurfewEnabled(ctx: Context, enabled: Boolean) { sp(ctx).edit().putBoolean(Curfew.KEY_ENABLED, enabled).apply() }

    fun getCurfewHour(ctx: Context) = sp(ctx).getInt(Curfew.KEY_HOUR, 22)
    fun getCurfewMin(ctx: Context)  = sp(ctx).getInt(Curfew.KEY_MIN, 0)
    fun setCurfewTime(ctx: Context, hour: Int, min: Int) {
        sp(ctx).edit().putInt(Curfew.KEY_HOUR, hour).putInt(Curfew.KEY_MIN, min).apply()
    }

    fun getCurfewDaysMask(ctx: Context) = sp(ctx).getInt(Curfew.KEY_DAYS_MASK, 0b1111111)
    fun setCurfewDaysMask(ctx: Context, mask: Int) { sp(ctx).edit().putInt(Curfew.KEY_DAYS_MASK, mask).apply() }

    fun skipCurfewToday(ctx: Context) { sp(ctx).edit().putBoolean(Curfew.KEY_SKIP_TODAY, true).apply() }
}
