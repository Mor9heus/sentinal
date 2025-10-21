package com.sentinal.app.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sentinal.app.ui.screen.PoliceWatchScreen
import com.sentinal.app.ui.screen.WatchModeScreen
import com.sentinal.app.ui.screen.simple.AdultHomeScreen

object SentinalRoutes {
    const val HOME_ADULT = "home_adult"
    const val POLICE_WATCH = "police_watch"
    const val WATCH_MODE = "watch_mode"
}

/**
 * Single flexible AppNav signature:
 * - AppNav()                               ✅
 * - AppNav(modifier = ...)                 ✅
 * - AppNav(nav = controller)               ✅
 * - AppNav(navController = controller)     ✅
 */
@Composable
fun AppNav(
    modifier: Modifier = Modifier,
    nav: NavHostController? = null,
    navController: NavHostController? = null
) {
    val controller = nav ?: navController ?: rememberNavController()

    NavHost(
        navController = controller,
        startDestination = SentinalRoutes.HOME_ADULT,
        modifier = modifier
    ) {
        composable(SentinalRoutes.HOME_ADULT) {
            // IMPORTANT: your AdultHomeScreen expects a 'nav' param
            AdultHomeScreen(nav = controller)
        }
        composable(SentinalRoutes.POLICE_WATCH) {
            PoliceWatchScreen(
                onBack = { controller.popBackStack() }
            )
        }
        // *** NEW: Register the missing watch_mode route ***
        composable(SentinalRoutes.WATCH_MODE) {
            WatchModeScreen(
                onBack = { controller.popBackStack() }
            )
        }
    }
}
