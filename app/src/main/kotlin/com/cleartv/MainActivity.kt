package com.cleartv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cleartv.ui.home.HomeScreen
import com.cleartv.ui.theme.ClearTVTheme

/**
 * Single Activity architecture for ClearTV.
 * Hosts a NavHost — Phase 1 has only the "home" destination.
 * Phase 2 will add "settings".
 *
 * Registered as CATEGORY_HOME in the manifest to replace the
 * Fire OS stock launcher.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Edge-to-edge: hide system bars for full TV experience
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            ClearTVTheme(darkTheme = false) { // Phase 1: light only
                ClearTVApp()
            }
        }
    }
}

@Composable
private fun ClearTVApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "home",
        modifier = Modifier.fillMaxSize(),
    ) {
        composable("home") {
            HomeScreen()
        }
        // Phase 2: composable("settings") { SettingsScreen() }
    }
}
