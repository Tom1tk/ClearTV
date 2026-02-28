package com.cleartv

import android.content.Context
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
import com.cleartv.data.AppInstallReceiver
import com.cleartv.data.PreferencesRepo
import com.cleartv.data.model.ThemeMode
import com.cleartv.data.model.UserPreferences
import com.cleartv.ui.home.HomeScreen
import com.cleartv.ui.home.HomeViewModel
import com.cleartv.ui.screensaver.ScreensaverOverlay
import com.cleartv.ui.screensaver.ScreensaverViewModel
import com.cleartv.ui.settings.SettingsScreen
import com.cleartv.ui.theme.ClearTVTheme

/**
 * Single Activity for ClearTV.
 * Hosts NavHost + screensaver overlay.
 * Registers a BroadcastReceiver for live app install/uninstall detection.
 * Intercepts all KeyEvents to reset the screensaver idle timer.
 */
class MainActivity : ComponentActivity() {

    private lateinit var preferencesRepo: PreferencesRepo
    private lateinit var screensaverViewModel: ScreensaverViewModel
    private lateinit var homeViewModel: HomeViewModel
    private var appInstallReceiver: AppInstallReceiver? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        preferencesRepo = PreferencesRepo(applicationContext)
        screensaverViewModel = ViewModelProvider(this)[ScreensaverViewModel::class.java]
        homeViewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        // Edge-to-edge
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.systemBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // Register for app install/uninstall broadcasts
        appInstallReceiver = AppInstallReceiver {
            homeViewModel.loadApps()
        }
        registerReceiver(appInstallReceiver, AppInstallReceiver.createFilter())

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
                ClearTVApp(
                    screensaverViewModel = screensaverViewModel,
                    homeViewModel = homeViewModel,
                )
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        appInstallReceiver?.let { unregisterReceiver(it) }
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        if (event.action == KeyEvent.ACTION_DOWN) {
            if (screensaverViewModel.isActive.value) {
                screensaverViewModel.dismiss()
                return true
            }
            screensaverViewModel.resetIdleTimer()
        }
        return super.dispatchKeyEvent(event)
    }
}

@Composable
private fun ClearTVApp(
    screensaverViewModel: ScreensaverViewModel,
    homeViewModel: HomeViewModel,
) {
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
                    viewModel = homeViewModel,
                    onNavigateToSettings = { navController.navigate("settings") },
                )
            }
            composable("settings") {
                SettingsScreen(
                    onNavigateBack = { navController.popBackStack() },
                )
            }
        }

        ScreensaverOverlay(
            isActive = screensaverActive,
            screensaverType = prefs.screensaverType,
            onDismiss = { screensaverViewModel.dismiss() },
        )
    }
}
