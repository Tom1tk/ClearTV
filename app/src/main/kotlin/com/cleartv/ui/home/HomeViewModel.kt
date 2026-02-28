package com.cleartv.ui.home

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cleartv.data.AppRepository
import com.cleartv.data.PreferencesRepo
import com.cleartv.data.WeatherRepository
import com.cleartv.data.model.AppInfo
import com.cleartv.data.model.ThemeMode
import com.cleartv.data.model.UserPreferences
import com.cleartv.data.model.WeatherData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel for the home screen. Manages installed apps, favourites,
 * hidden apps, weather, and theme — all persisted via DataStore.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    val preferencesRepo = PreferencesRepo(application)
    private val weatherRepository = WeatherRepository()

    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Context menu
    private val _contextMenuApp = MutableStateFlow<AppInfo?>(null)
    val contextMenuApp: StateFlow<AppInfo?> = _contextMenuApp.asStateFlow()

    // Weather
    private val _weather = MutableStateFlow<WeatherData?>(null)
    val weather: StateFlow<WeatherData?> = _weather.asStateFlow()

    private val _weatherLocationName = MutableStateFlow("")
    val weatherLocationName: StateFlow<String> = _weatherLocationName.asStateFlow()

    val preferences: StateFlow<UserPreferences> = preferencesRepo.preferences
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    val visibleApps: StateFlow<List<AppInfo>> = combine(
        _allApps,
        preferencesRepo.preferences,
    ) { apps, prefs ->
        apps.filter { it.packageName !in prefs.hiddenPackages }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    val favourites: StateFlow<List<AppInfo>> = combine(
        _allApps,
        preferencesRepo.preferences,
    ) { apps, prefs ->
        if (prefs.favouritePackages.isEmpty()) {
            val defaults = listOf(
                "com.netflix.ninja",
                "com.google.android.youtube.tv",
                "com.disney.disneyplus",
                "com.amazon.avod",
            )
            defaults.mapNotNull { pkg -> apps.find { it.packageName == pkg } }
                .ifEmpty { apps.take(4) }
        } else {
            prefs.favouritePackages.mapNotNull { pkg ->
                apps.find { it.packageName == pkg }
            }
        }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {
        loadApps()
        startWeatherUpdates()
    }

    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            _allApps.value = repository.getInstalledApps()
            _isLoading.value = false
        }
    }

    fun getLaunchIntent(app: AppInfo): Intent? =
        repository.getLaunchIntent(app.packageName)

    // ── Weather ──────────────────────────────────────────────────────────────

    private fun startWeatherUpdates() {
        viewModelScope.launch {
            while (true) {
                fetchWeather()
                delay(30 * 60 * 1000L) // 30 minutes
            }
        }
    }

    private suspend fun fetchWeather() {
        val prefs = preferencesRepo.preferences.first()
        if (!prefs.showWeather) return

        // Determine coordinates
        val location = prefs.weatherLocation
        val (lat, lon, name) = if (location.isNotEmpty()) {
            val geo = weatherRepository.geocode(location).getOrNull()
            if (geo != null) {
                Triple(geo.latitude, geo.longitude, geo.name)
            } else {
                // Fallback: London
                Triple(51.5074, -0.1278, "London")
            }
        } else {
            // Default: London (IP-based geolocation would go here)
            Triple(51.5074, -0.1278, "London")
        }

        _weatherLocationName.value = name

        weatherRepository.getWeather(
            latitude = lat,
            longitude = lon,
            useCelsius = prefs.weatherCelsius,
        ).onSuccess { data ->
            _weather.value = data.copy(locationName = name)
        }
    }

    // ── Context menu ─────────────────────────────────────────────────────────

    fun showContextMenu(app: AppInfo) { _contextMenuApp.value = app }
    fun dismissContextMenu() { _contextMenuApp.value = null }

    fun toggleFavourite(packageName: String) {
        viewModelScope.launch { preferencesRepo.toggleFavourite(packageName) }
    }

    fun hideApp(packageName: String) {
        viewModelScope.launch {
            preferencesRepo.hideApp(packageName)
            _contextMenuApp.value = null
        }
    }

    fun unhideApp(packageName: String) {
        viewModelScope.launch { preferencesRepo.unhideApp(packageName) }
    }

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch { preferencesRepo.setTheme(theme) }
    }
}
