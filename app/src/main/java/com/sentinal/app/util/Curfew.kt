package com.sentinal.app.util

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import java.util.Calendar

object Curfew {
    // SharedPrefs keys
    const val PREFS = "profile_prefs"
    const val KEY_ENABLED = "curfew_enabled"
    const val KEY_HOUR = "curfew_hour"        // 0..23
    const val KEY_MIN = "curfew_min"          // 0..59
    const val KEY_DAYS_MASK = "curfew_days"   // bit0=Sun ... bit6=Sat
    const val KEY_SKIP_TODAY = "curfew_skip_today"

    // Receiver action
    const val ACTION_CURFEW_ALERT = "com.sentinal.app.action.CURFEW_ALERT"

    /** Set or update the next alarm for T-30min before configured curfew time on the next matching day. */
    fun scheduleNext(ctx: Context) {
        val sp = ctx.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        if (!sp.getBoolean(KEY_ENABLED, false)) {
            cancel(ctx); return
        }
        val hour = sp.getInt(KEY_HOUR, 22)      // default 22:00
        val min  = sp.getInt(KEY_MIN,  0)
        val mask = sp.getInt(KEY_DAYS_MASK, 0b1111111) // default all days on
        val skipTodayOnce = sp.getBoolean(KEY_SKIP_TODAY, false)

        val now = Calendar.getInstance()
        val next = Calendar.getInstance().apply {
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }

        // Find next day from today that is enabled
        for (i in 0..7) {
            val c = (now.clone() as Calendar).apply { add(Calendar.DAY_OF_YEAR, i) }
            val dowBit = dayOfWeekBit(c.get(Calendar.DAY_OF_WEEK))
            if ((mask and dowBit) == 0) continue // this day disabled

            // If today and skip requested, skip
            if (i == 0 && skipTodayOnce) continue

            // Curfew at hour:min, reminder at T-30
            c.set(Calendar.HOUR_OF_DAY, hour)
            c.set(Calendar.MINUTE, min)
            c.set(Calendar.SECOND, 0)
            c.set(Calendar.MILLISECOND, 0)
            c.add(Calendar.MINUTE, -30)

            // If this reminder time has already passed and i == 0, look to next enabled day
            if (i == 0 && c.timeInMillis <= now.timeInMillis) continue

            next.timeInMillis = c.timeInMillis
            break
        }

        // Clear skip flag if it was set
        if (skipTodayOnce) sp.edit().putBoolean(KEY_SKIP_TODAY, false).apply()

        // Schedule via AlarmClockInfo to avoid SCHEDULE_EXACT_ALARM permission
        val am = ctx.getSystemService(AlarmManager::class.java)
        val pi = pending(ctx)

        val info = AlarmManager.AlarmClockInfo(next.timeInMillis, contentIntent(ctx))
        am.setAlarmClock(info, pi)
    }

    /** Cancel any scheduled curfew reminder. */
    fun cancel(ctx: Context) {
        val am = ctx.getSystemService(AlarmManager::class.java)
        am.cancel(pending(ctx))
    }

    /** Fire an immediate test alert. */
    fun fireTest(ctx: Context) {
        ctx.sendBroadcast(Intent(ACTION_CURFEW_ALERT).setPackage(ctx.packageName))
    }

    // Helpers
    private fun dayOfWeekBit(dow: Int): Int = when (dow) {
        Calendar.SUNDAY    -> 1 shl 0
        Calendar.MONDAY    -> 1 shl 1
        Calendar.TUESDAY   -> 1 shl 2
        Calendar.WEDNESDAY -> 1 shl 3
        Calendar.THURSDAY  -> 1 shl 4
        Calendar.FRIDAY    -> 1 shl 5
        Calendar.SATURDAY  -> 1 shl 6
        else -> 0
    }

    private fun pending(ctx: Context): PendingIntent {
        val i = Intent(ACTION_CURFEW_ALERT).setPackage(ctx.packageName)
        return PendingIntent.getBroadcast(
            ctx, 9001, i, PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun contentIntent(ctx: Context): PendingIntent {
        // Reuse same PI for the status-bar clock affordance; just opens the broadcast.
        val i = Intent(ACTION_CURFEW_ALERT).setPackage(ctx.packageName)
        return PendingIntent.getBroadcast(ctx, 9002, i, PendingIntent.FLAG_IMMUTABLE)
    }
}
