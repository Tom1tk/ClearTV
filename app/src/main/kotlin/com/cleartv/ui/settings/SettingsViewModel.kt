package com.cleartv.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cleartv.data.AppRepository
import com.cleartv.data.PreferencesRepo
import com.cleartv.data.model.AppInfo
import com.cleartv.data.model.ScreensaverType
import com.cleartv.data.model.ThemeMode
import com.cleartv.data.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepo = PreferencesRepo(application)
    private val appRepository = AppRepository(application)

    val preferences: StateFlow<UserPreferences> = preferencesRepo.preferences
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())

    val hiddenApps: StateFlow<List<AppInfo>> = combine(
        _allApps, preferencesRepo.preferences,
    ) { apps, prefs ->
        prefs.hiddenPackages.mapNotNull { pkg -> apps.find { it.packageName == pkg } }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val favouriteApps: StateFlow<List<AppInfo>> = combine(
        _allApps, preferencesRepo.preferences,
    ) { apps, prefs ->
        prefs.favouritePackages.mapNotNull { pkg -> apps.find { it.packageName == pkg } }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        viewModelScope.launch { _allApps.value = appRepository.getInstalledApps() }
    }

    // Appearance
    fun setTheme(theme: ThemeMode) { viewModelScope.launch { preferencesRepo.setTheme(theme) } }
    fun setBlurIntensity(level: Int) { viewModelScope.launch { preferencesRepo.setBlurIntensity(level) } }
    fun setShowClock(show: Boolean) { viewModelScope.launch { preferencesRepo.setShowClock(show) } }
    fun setShowWeather(show: Boolean) { viewModelScope.launch { preferencesRepo.setShowWeather(show) } }

    // Apps
    fun setShowSystemApps(show: Boolean) { viewModelScope.launch { preferencesRepo.setShowSystemApps(show) } }
    fun unhideApp(pkg: String) { viewModelScope.launch { preferencesRepo.unhideApp(pkg) } }
    fun removeFavourite(pkg: String) { viewModelScope.launch { preferencesRepo.toggleFavourite(pkg) } }

    // Weather
    fun setWeatherLocation(location: String) { viewModelScope.launch { preferencesRepo.setWeatherLocation(location) } }
    fun setWeatherCelsius(celsius: Boolean) { viewModelScope.launch { preferencesRepo.setWeatherCelsius(celsius) } }
    fun setWeather12hr(use12hr: Boolean) { viewModelScope.launch { preferencesRepo.setWeather12hr(use12hr) } }

    // Screensaver
    fun setScreensaverEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesRepo.update { it.copy(screensaverEnabled = enabled) } }
    }
    fun setScreensaverTimeout(minutes: Int) {
        viewModelScope.launch { preferencesRepo.update { it.copy(screensaverTimeoutMin = minutes) } }
    }
    fun setScreensaverType(type: ScreensaverType) {
        viewModelScope.launch { preferencesRepo.update { it.copy(screensaverType = type) } }
    }

    fun restoreDefaults() { viewModelScope.launch { preferencesRepo.restoreDefaults() } }
}
