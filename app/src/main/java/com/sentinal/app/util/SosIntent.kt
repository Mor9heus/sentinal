package com.sentinal.app.util

import android.content.Context
import android.content.Intent
import android.net.Uri

object SosIntent {
    /**
     * Opens the user’s SMS app with recipients prefilled and message body.
     * No SEND_SMS permission needed.
     */
    fun sendSmsToContacts(ctx: Context, numbers: List<String>, body: String) {
        val filtered = numbers.filter { it.isNotBlank() }
        if (filtered.isEmpty()) return
        val uri = Uri.parse("smsto:" + filtered.joinToString(";"))
        val intent = Intent(Intent.ACTION_SENDTO, uri).apply {
            putExtra("sms_body", body)
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
    }

    /** Opens dialer with 911 (no CALL_PHONE permission required). */
    fun dial911(ctx: Context) {
        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:911"))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        ctx.startActivity(intent)
    }
}
