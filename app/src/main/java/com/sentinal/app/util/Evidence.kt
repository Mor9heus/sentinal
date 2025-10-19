package com.sentinal.app.util

import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import org.json.JSONObject
import java.io.InputStream
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

data class EvidenceResult(
    val mediaUri: Uri,
    val jsonUri: Uri,
    val mediaSha256: String
)

object Evidence {
    /** Build an evidence JSON in Downloads with metadata + SHA-256 of the captured media. */
    fun generate(ctx: Context, media: Uri, eventType: String, notes: String): EvidenceResult {
        val sha = sha256OfUri(ctx, media)
        val now = System.currentTimeMillis()
        val iso = iso8601(now)

        val json = JSONObject().apply {
            put("event_type", eventType)
            put("timestamp", iso)
            put("media_uri", media.toString())
            put("media_sha256", sha)
            put("device_id_hash", SafeIds.deviceIdHash(ctx))
            put("location", JSONObject().apply {
                put("lat", JSONObject.NULL) // TODO: add real lat
                put("lng", JSONObject.NULL) // TODO: add real lng
            })
            put("notes", notes)
            put("app", JSONObject().apply {
                put("name", "Sentinal")
                put("version", "0.1")
            })
        }

        val fileName = "sentinal_evidence_${now}.json"
        val jsonUri = saveJsonToDownloads(ctx, fileName, json.toString(2))
        return EvidenceResult(mediaUri = media, jsonUri = jsonUri, mediaSha256 = sha)
    }

    private fun iso8601(ms: Long): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)
        sdf.timeZone = TimeZone.getTimeZone("UTC")
        return sdf.format(Date(ms))
    }

    private fun saveJsonToDownloads(ctx: Context, name: String, payload: String): Uri {
        val values = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, name)
            put(MediaStore.Downloads.MIME_TYPE, "application/json")
        }
        val resolver = ctx.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
            ?: throw IllegalStateException("Unable to create evidence JSON")
        resolver.openOutputStream(uri)?.use { out ->
            out.write(payload.toByteArray(Charsets.UTF_8))
            out.flush()
        }
        return uri
    }

    private fun sha256OfUri(ctx: Context, uri: Uri): String {
        val md = MessageDigest.getInstance("SHA-256")
        ctx.contentResolver.openInputStream(uri)?.use { stream: InputStream ->
            val buf = ByteArray(8192)
            while (true) {
                val n = stream.read(buf)
                if (n <= 0) break
                md.update(buf, 0, n)
            }
        } ?: return ""
        return md.digest().joinToString("") { b -> "%02x".format(b) }
    }
}

private object SafeIds {
    fun deviceIdHash(ctx: Context): String {
        val id = android.provider.Settings.Secure.getString(
            ctx.contentResolver,
            android.provider.Settings.Secure.ANDROID_ID
        ) ?: "unknown"
        val input = "$id|${ctx.packageName}"
        val md = MessageDigest.getInstance("SHA-256")
        return md.digest(input.toByteArray(Charsets.UTF_8)).joinToString("") { "%02x".format(it) }
    }
}
