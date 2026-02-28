package com.cleartv.ui.home

import android.app.Application
import android.content.Intent
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
 * ViewModel for the home screen. Manages installed apps, favourites,
 * hidden apps, and theme state — all persisted via DataStore.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    val preferencesRepo = PreferencesRepo(application)

    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Context menu state
    private val _contextMenuApp = MutableStateFlow<AppInfo?>(null)
    val contextMenuApp: StateFlow<AppInfo?> = _contextMenuApp.asStateFlow()

    /**
     * User preferences flow — emits whenever DataStore changes.
     */
    val preferences: StateFlow<UserPreferences> = preferencesRepo.preferences
        .stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())

    /**
     * Visible apps — filters out hidden packages.
     */
    val visibleApps: StateFlow<List<AppInfo>> = combine(
        _allApps,
        preferencesRepo.preferences,
    ) { apps, prefs ->
        apps.filter { it.packageName !in prefs.hiddenPackages }
    }.stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    /**
     * Favourites — resolved from saved package names to AppInfo objects.
     */
    val favourites: StateFlow<List<AppInfo>> = combine(
        _allApps,
        preferencesRepo.preferences,
    ) { apps, prefs ->
        if (prefs.favouritePackages.isEmpty()) {
            // Auto-detect defaults if no favourites set
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

    // ── Context menu actions ─────────────────────────────────────────────────

    fun showContextMenu(app: AppInfo) {
        _contextMenuApp.value = app
    }

    fun dismissContextMenu() {
        _contextMenuApp.value = null
    }

    fun toggleFavourite(packageName: String) {
        viewModelScope.launch {
            preferencesRepo.toggleFavourite(packageName)
        }
    }

    fun hideApp(packageName: String) {
        viewModelScope.launch {
            preferencesRepo.hideApp(packageName)
            _contextMenuApp.value = null
        }
    }

    fun unhideApp(packageName: String) {
        viewModelScope.launch {
            preferencesRepo.unhideApp(packageName)
        }
    }

    fun setTheme(theme: ThemeMode) {
        viewModelScope.launch {
            preferencesRepo.setTheme(theme)
        }
    }
}
