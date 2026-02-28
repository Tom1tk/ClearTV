package com.cleartv.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cleartv.data.AppRepository
import com.cleartv.data.PreferencesRepo
import com.cleartv.data.model.AppInfo
import com.cleartv.data.model.ThemeMode
import com.cleartv.data.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the Settings screen.
 * Reads/writes preferences and provides hidden apps list.
 */
class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferencesRepo = PreferencesRepo(application)
    private val appRepository = AppRepository(application)

    val preferences: StateFlow<UserPreferences> = preferencesRepo.preferences
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())

    /**
     * Hidden apps resolved to their AppInfo — for display in the settings list.
     */
    val hiddenApps: StateFlow<List<AppInfo>> = combine(
        _allApps,
        preferencesRepo.preferences,
    ) { apps, prefs ->
        prefs.hiddenPackages.mapNotNull { pkg ->
            apps.find { it.packageName == pkg }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * Favourite apps resolved to AppInfo.
     */
    val favouriteApps: StateFlow<List<AppInfo>> = combine(
        _allApps,
        preferencesRepo.preferences,
    ) { apps, prefs ->
        prefs.favouritePackages.mapNotNull { pkg ->
            apps.find { it.packageName == pkg }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        viewModelScope.launch {
            _allApps.value = appRepository.getInstalledApps()
        }
    }

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch { preferencesRepo.setTheme(theme) }
    }

    fun setShowWeather(show: Boolean) {
        viewModelScope.launch { preferencesRepo.setShowWeather(show) }
    }

    fun setShowClock(show: Boolean) {
        viewModelScope.launch { preferencesRepo.setShowClock(show) }
    }

    fun setBlurIntensity(level: Int) {
        viewModelScope.launch { preferencesRepo.setBlurIntensity(level) }
    }

    fun setShowSystemApps(show: Boolean) {
        viewModelScope.launch { preferencesRepo.setShowSystemApps(show) }
    }

    fun unhideApp(packageName: String) {
        viewModelScope.launch { preferencesRepo.unhideApp(packageName) }
    }

    fun removeFavourite(packageName: String) {
        viewModelScope.launch { preferencesRepo.toggleFavourite(packageName) }
    }

    fun restoreDefaults() {
        viewModelScope.launch { preferencesRepo.restoreDefaults() }
    }
}
