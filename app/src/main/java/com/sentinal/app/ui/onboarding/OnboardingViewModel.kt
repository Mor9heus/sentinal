package com.sentinal.app.ui.onboarding

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import com.sentinal.app.ui.Routes

class OnboardingViewModel(app: Application) : AndroidViewModel(app) {
    private val sp = app.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    fun isProfileComplete(): Boolean = sp.getBoolean(KEY_PROFILE_COMPLETE, false)

    fun setProfileComplete() {
        sp.edit().putBoolean(KEY_PROFILE_COMPLETE, true).remove(KEY_ONBOARD_STEP).apply()
    }

    fun saveRole(role: String) { sp.edit().putString(KEY_PROFILE_ROLE, role).apply() }

    fun getRole(): String = sp.getString(KEY_PROFILE_ROLE, "ADULT") ?: "ADULT"

    fun roleHomeRoute(): String = when (getRole()) {
        "CHILD" -> Routes.HOME_CHILD
        "ELDERLY" -> Routes.HOME_ELDER
        "CAREGIVER" -> Routes.HOME_CAREGIVER
        "TEEN" -> Routes.HOME_ADULT // teen shares adult home for now
        else -> Routes.HOME_ADULT
    }

    fun nextFrom(step: String): String = when (step) {
        Routes.SPLASH -> Routes.WELCOME
        Routes.WELCOME -> Routes.ROLE
        Routes.ROLE -> Routes.PERMISSIONS
        Routes.PERMISSIONS -> Routes.CONSENT
        Routes.CONSENT -> Routes.SAFETY_PROFILE
        Routes.SAFETY_PROFILE -> Routes.PAIRING
        Routes.PAIRING -> Routes.CONTACTS_ROLES
        Routes.CONTACTS_ROLES -> if (getRole() == "TEEN") Routes.TEEN_CURFEW else Routes.ATTORNEY
        Routes.TEEN_CURFEW -> Routes.ATTORNEY
        Routes.ATTORNEY -> Routes.MODE_PRESETS
        Routes.MODE_PRESETS -> Routes.BATTERY_BG
        Routes.BATTERY_BG -> Routes.ENCRYPTION
        Routes.ENCRYPTION -> Routes.QUICK_TEST
        Routes.QUICK_TEST -> Routes.FINAL_REVIEW
        else -> Routes.FINAL_REVIEW
    }

    fun markStep(name: String) { sp.edit().putString(KEY_ONBOARD_STEP, name).apply() }
    fun currentStep(): String? = sp.getString(KEY_ONBOARD_STEP, null)

    fun setPermissionsSeen() { sp.edit().putBoolean(KEY_PERMISSIONS_SEEN, true).apply() }
    fun setConsentAccepted() { sp.edit().putBoolean(KEY_CONSENT_ACCEPTED, true).apply() }
}

const val PREFS = "profile_prefs"
const val KEY_PROFILE_COMPLETE = "profile_complete"
const val KEY_ONBOARD_STEP = "onboard_step"
const val KEY_PERMISSIONS_SEEN = "permissions_seen"
const val KEY_CONSENT_ACCEPTED = "consent_accepted"
const val KEY_PROFILE_ROLE = "profile_role"
