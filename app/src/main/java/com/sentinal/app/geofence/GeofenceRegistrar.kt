package com.sentinal.app.geofence

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.Geofence
import com.google.android.gms.location.GeofencingClient
import com.google.android.gms.location.GeofencingRequest
import com.google.android.gms.location.LocationServices

object GeofenceRegistrar {
    private const val REQ_CODE = 9911

    fun geofencingClient(ctx: Context): GeofencingClient =
        LocationServices.getGeofencingClient(ctx)

    fun pendingIntent(ctx: Context): PendingIntent {
        val i = Intent(ctx, GeofenceReceiver::class.java)
        return PendingIntent.getBroadcast(
            ctx, REQ_CODE, i,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun registerAll(ctx: Context) {
        if (!hasLocation(ctx)) return
        val client = geofencingClient(ctx)
        // First remove previous to avoid duplicates
        client.removeGeofences(pendingIntent(ctx))

        val places = TrustedPlacesStore.load(ctx)
        if (places.isEmpty()) return

        val geofences = places.mapIndexed { idx, p ->
            Geofence.Builder()
                .setRequestId("trusted_${idx}_${p.name}")
                .setCircularRegion(p.lat, p.lng, p.radiusM)
                .setTransitionTypes(
                    Geofence.GEOFENCE_TRANSITION_ENTER or Geofence.GEOFENCE_TRANSITION_EXIT
                )
                .setExpirationDuration(Geofence.NEVER_EXPIRE)
                .build()
        }

        val req = GeofencingRequest.Builder()
            .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER or GeofencingRequest.INITIAL_TRIGGER_EXIT)
            .addGeofences(geofences)
            .build()

        client.addGeofences(req, pendingIntent(ctx))
    }

    private fun hasLocation(ctx: Context): Boolean {
        val fine = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }
}
