package com.sentinal.app.ui.screen.trusted

/**
 * A simple geofence definition stored locally.
 * lat/lng in decimal degrees, radius in meters.
 */
data class TrustedPlace(
    val name: String = "",
    val lat: Double = 0.0,
    val lng: Double = 0.0,
    val radiusM: Float = 150f
)
