package com.sentinal.app.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sentinal.app.ui.onboarding.*
import com.sentinal.app.ui.screen.ContactsScreen
import com.sentinal.app.ui.screen.SettingsScreen
import com.sentinal.app.ui.screen.simple.*
import com.sentinal.app.ui.screen.trusted.TrustedPlacesScreen
import com.sentinal.app.ui.screen.watch.WatchModeScreen

@Composable
fun AppNav(nav: NavHostController = rememberNavController()) {
    val vm: OnboardingViewModel = viewModel()

    val start = if (vm.isProfileComplete()) when (vm.roleHomeRoute()) {
        Routes.HOME_CHILD -> Routes.HOME_CHILD
        Routes.HOME_ELDER -> Routes.HOME_ELDER
        Routes.HOME_CAREGIVER -> Routes.HOME_CAREGIVER
        else -> Routes.HOME_ADULT
    } else Routes.SPLASH

    NavHost(navController = nav, startDestination = start) {
        // Onboarding
        composable(Routes.SPLASH) { SplashScreen(nav, vm) }
        composable(Routes.WELCOME) { WelcomeScreen(nav) }
        composable(Routes.ROLE) { RoleSelectScreen(nav, vm) }
        composable(Routes.PERMISSIONS) { PermissionsPrimerScreen(nav, vm) }
        composable(Routes.CONSENT) { ConsentTutorialScreen(nav, vm) }
        composable(Routes.SAFETY_PROFILE) { SafetyProfileScreen(nav, vm) }
        composable(Routes.PAIRING) { PairingScreen(nav, vm) }
        composable(Routes.CONTACTS_ROLES) { ContactsRolesScreen(nav, vm) }
        composable(Routes.TEEN_CURFEW) { TeenCurfewScreen(nav, vm) }
        composable(Routes.ATTORNEY) { AttorneyScreen(nav, vm) }
        composable(Routes.MODE_PRESETS) { ModePresetsScreen(nav, vm) }
        composable(Routes.BATTERY_BG) { BatteryBackgroundScreen(nav, vm) }
        composable(Routes.ENCRYPTION) { EncryptionScreen(nav, vm) }
        composable(Routes.QUICK_TEST) { QuickTestScreen(nav, vm) }
        composable(Routes.FINAL_REVIEW) { FinalReviewScreen(nav, vm) }

        // Utility
        composable(Routes.CONTACTS) { ContactsScreen(nav) }
        composable(Routes.SETTINGS) { SettingsScreen(nav) }
        composable(Routes.TRUSTED_PLACES) { TrustedPlacesScreen(nav) }

        // Watch Mode
        composable(Routes.WATCH_MODE) { WatchModeScreen(nav) }

        // Homes
        composable(Routes.HOME_ADULT) { AdultHomeScreen(nav) }
        composable(Routes.HOME_CHILD) { ChildHomeScreen(nav) }
        composable(Routes.HOME_ELDER) { ElderHomeScreen(nav) }
        composable(Routes.HOME_CAREGIVER) { CaregiverHomeScreen(nav) }
    }
}
