package com.cleartv.ui.home

import android.app.Application
import android.content.Intent
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.cleartv.data.AppRepository
import com.cleartv.data.model.AppInfo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the home screen. Manages the list of installed apps
 * and favourites. Phase 1: favourites are the first 4 apps;
 * Phase 2 will wire favourites to DataStore.
 */
class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)

    private val _allApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val allApps: StateFlow<List<AppInfo>> = _allApps.asStateFlow()

    private val _favourites = MutableStateFlow<List<AppInfo>>(emptyList())
    val favourites: StateFlow<List<AppInfo>> = _favourites.asStateFlow()

    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Well-known streaming packages used to auto-populate favourites in Phase 1
    private val defaultFavouritePackages = listOf(
        "com.netflix.ninja",           // Netflix (Fire TV)
        "com.google.android.youtube.tv", // YouTube (TV)
        "com.disney.disneyplus",       // Disney+
        "com.amazon.avod",             // Prime Video
    )

    init {
        loadApps()
    }

    fun loadApps() {
        viewModelScope.launch {
            _isLoading.value = true
            val apps = repository.getInstalledApps()
            _allApps.value = apps

            // Phase 1: auto-detect favourites from installed streaming apps
            val favs = defaultFavouritePackages.mapNotNull { pkg ->
                apps.find { it.packageName == pkg }
            }
            // If none of the defaults are installed, take first 4
            _favourites.value = favs.ifEmpty { apps.take(4) }

            _isLoading.value = false
        }
    }

    /**
     * Returns a launch intent for the given app, or null.
     */
    fun getLaunchIntent(app: AppInfo): Intent? {
        return repository.getLaunchIntent(app.packageName)
    }
}
