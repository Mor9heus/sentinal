package com.sentinal.app.util.model
import java.time.*
data class LatLngRadius(val lat: Double, val lng: Double, val meters: Float)
enum class ProfileType { ADULT, TEEN, CHILD, ELDERLY, CAREGIVER }
data class CurfewPolicy(
    val days: Set<DayOfWeek> = emptySet(),
    val start: LocalTime? = null,
    val end: LocalTime? = null,
    val exemptions: List<LocalDate> = emptyList()
)
data class GeofencePolicy(
    val trusted: List<LatLngRadius> = emptyList(),
    val autoArmOnExit: Boolean = true
)
data class PairingState(
    val linkedIds: List<String> = emptyList(),
    val homeSsid: String? = null
)
