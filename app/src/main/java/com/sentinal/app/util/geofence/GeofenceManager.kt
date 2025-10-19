package com.sentinal.app.util.geofence
import com.sentinal.app.util.model.*
interface GeofenceManager { fun policyFor(profile: ProfileType): GeofencePolicy }
class GeofenceManagerStub : GeofenceManager {
    override fun policyFor(profile: ProfileType) = when (profile) {
        ProfileType.ADULT -> GeofencePolicy(autoArmOnExit = false)
        ProfileType.CAREGIVER -> GeofencePolicy(autoArmOnExit = true)
        else -> GeofencePolicy(autoArmOnExit = true)
    }
}
