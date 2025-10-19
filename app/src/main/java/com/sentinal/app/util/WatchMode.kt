package com.sentinal.app.util

import android.content.Context

/**
 * Simple state holder + presets for Watch Mode.
 * Impact/Water detection is always-on in GuardianService; Watch Mode enables active listening sensitivity.
 */
object WatchMode {
    const val PREFS = "profile_prefs"

    const val KEY_ACTIVE = "watch_active"
    const val KEY_MODE = "watch_mode"              // JOGGING, WORKOUT, CONCERT, DATE, POLICE, CHURCH, OTHER
    const val KEY_SENSITIVITY = "watch_sensitivity"// 0..100 (UI)
    const val KEY_DATE_NAME = "watch_date_name"
    const val KEY_DATE_PHONE = "watch_date_phone"
    const val KEY_DATE_WHERE = "watch_date_where"
    const val KEY_DATE_PHOTO = "watch_date_photo_path"

    enum class Mode { JOGGING, WORKOUT, CONCERT, DATE, POLICE, CHURCH, OTHER }

    private fun sp(ctx: Context) = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isActive(ctx: Context) = sp(ctx).getBoolean(KEY_ACTIVE, false)
    fun currentMode(ctx: Context): Mode = runCatching {
        Mode.valueOf(sp(ctx).getString(KEY_MODE, Mode.OTHER.name) ?: Mode.OTHER.name)
    }.getOrDefault(Mode.OTHER)

    fun sensitivity(ctx: Context) = sp(ctx).getInt(KEY_SENSITIVITY, defaultSensitivity(currentMode(ctx)))

    fun start(ctx: Context, mode: Mode, sensitivity: Int? = null) {
        val s = sensitivity ?: defaultSensitivity(mode)
        sp(ctx).edit()
            .putBoolean(KEY_ACTIVE, true)
            .putString(KEY_MODE, mode.name)
            .putInt(KEY_SENSITIVITY, s)
            .apply()
        // Tell the service to apply the profile (service is already on for impact/water)
        GuardianServiceControl.applyWatchProfile(ctx, mode.name, s)
    }

    fun stop(ctx: Context) {
        sp(ctx).edit().putBoolean(KEY_ACTIVE, false).apply()
        GuardianServiceControl.applyWatchProfile(ctx, "OFF", 0)
    }

    fun setDateDetails(ctx: Context, name: String, phone: String, where: String, photoPath: String?) {
        sp(ctx).edit()
            .putString(KEY_DATE_NAME, name)
            .putString(KEY_DATE_PHONE, phone)
            .putString(KEY_DATE_WHERE, where)
            .putString(KEY_DATE_PHOTO, photoPath)
            .apply()
    }

    fun getDateName(ctx: Context)  = sp(ctx).getString(KEY_DATE_NAME, "") ?: ""
    fun getDatePhone(ctx: Context) = sp(ctx).getString(KEY_DATE_PHONE, "") ?: ""
    fun getDateWhere(ctx: Context) = sp(ctx).getString(KEY_DATE_WHERE, "") ?: ""
    fun getDatePhoto(ctx: Context) = sp(ctx).getString(KEY_DATE_PHOTO, null)

    fun defaultSensitivity(mode: Mode): Int = when (mode) {
        Mode.JOGGING -> 25    // lowest — ignore repetitive thumps/breathing
        Mode.WORKOUT -> 35
        Mode.CONCERT -> 70    // higher in noisy/crowded places
        Mode.DATE    -> 55    // normal-high, conversation-aware
        Mode.POLICE  -> 80    // high, but Police recording handled on separate screen too
        Mode.CHURCH  -> 45
        Mode.OTHER   -> 50
    }
}

/** Messaging from UI→service without tight coupling (no binder). */
object GuardianServiceControl {
    fun applyWatchProfile(ctx: Context, modeName: String, sensitivity: Int) {
        // Could post a broadcast or write to prefs for service to poll; keep simple with prefs flag.
        ctx.getSharedPreferences(WatchMode.PREFS, Context.MODE_PRIVATE).edit()
            .putString("svc_mode_name", modeName)
            .putInt("svc_sensitivity", sensitivity)
            .putLong("svc_last_update", System.currentTimeMillis())
            .apply()
    }
}
