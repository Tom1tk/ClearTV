package com.cleartv

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cleartv.data.PreferencesRepo
import com.cleartv.data.model.ThemeMode
import com.cleartv.data.model.UserPreferences
import com.cleartv.ui.home.HomeScreen
import com.cleartv.ui.settings.SettingsScreen
import com.cleartv.ui.theme.ClearTVTheme

/**
 * Single Activity architecture for ClearTV.
 * Hosts a NavHost with "home" and "settings" destinations.
 * Theme is driven by DataStore preferences — live recomposition on change.
 */
class MainActivity : ComponentActivity() {

    private lateinit var preferencesRepo: PreferencesRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesRepo = PreferencesRepo(applicationContext)

        // Edge-to-edge: hide system bars for full TV experience
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        setContent {
            val preferences by preferencesRepo.preferences.collectAsState(
                initial = UserPreferences()
            )

            val isDark = when (preferences.theme) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            ClearTVTheme(darkTheme = isDark) {
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
            HomeScreen(
                onNavigateToSettings = { navController.navigate("settings") },
            )
        }
        composable("settings") {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
            )
        }
    }
}
