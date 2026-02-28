package com.cleartv

import android.os.Bundle
import android.view.KeyEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cleartv.data.PreferencesRepo
import com.cleartv.data.model.ThemeMode
import com.cleartv.data.model.UserPreferences
import com.cleartv.ui.home.HomeScreen
import com.cleartv.ui.screensaver.ScreensaverOverlay
import com.cleartv.ui.screensaver.ScreensaverViewModel
import com.cleartv.ui.settings.SettingsScreen
import com.cleartv.ui.theme.ClearTVTheme

/**
 * Single Activity for ClearTV.
 * Hosts NavHost + screensaver overlay. Intercepts all KeyEvents
 * to reset the screensaver idle timer.
 */
class MainActivity : ComponentActivity() {

    private lateinit var preferencesRepo: PreferencesRepo
    private lateinit var screensaverViewModel: ScreensaverViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesRepo = PreferencesRepo(applicationContext)
        screensaverViewModel = ViewModelProvider(this)[ScreensaverViewModel::class.java]

        // Edge-to-edge
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
                ClearTVApp(screensaverViewModel = screensaverViewModel)
            }
        }
    }

    /**
     * Intercept ALL key events to reset the screensaver idle timer.
     * If screensaver is active, dismiss it on any press.
     */
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if (event?.action == KeyEvent.ACTION_DOWN) {
            if (screensaverViewModel.isActive.value) {
                screensaverViewModel.dismiss()
                return true // consume the key
            }
            screensaverViewModel.resetIdleTimer()
        }
        return super.dispatchKeyEvent(event)
    }
}

@Composable
private fun ClearTVApp(screensaverViewModel: ScreensaverViewModel) {
    val navController = rememberNavController()
    val screensaverActive by screensaverViewModel.isActive.collectAsState()
    val prefs by screensaverViewModel.preferences.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
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

        // Screensaver overlay — on top of everything
        ScreensaverOverlay(
            isActive = screensaverActive,
            screensaverType = prefs.screensaverType,
            onDismiss = { screensaverViewModel.dismiss() },
        )
    }
}
