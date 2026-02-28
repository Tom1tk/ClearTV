package com.cleartv.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.cleartv.data.model.ThemeMode
import com.cleartv.data.model.UserPreferences
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

/**
 * Persistence layer wrapping DataStore<Preferences>.
 * Stores the entire UserPreferences as a single JSON string
 * for simplicity — avoids individual keys for 16+ fields.
 */

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "cleartv_prefs")

class PreferencesRepo(private val context: Context) {

    companion object {
        private val PREFS_KEY = stringPreferencesKey("user_preferences")
        private val json = Json { ignoreUnknownKeys = true }
    }

    /**
     * Emits the current UserPreferences whenever they change.
     */
    val preferences: Flow<UserPreferences> = context.dataStore.data.map { prefs ->
        val raw = prefs[PREFS_KEY]
        if (raw != null) {
            try {
                json.decodeFromString<UserPreferences>(raw)
            } catch (_: Exception) {
                UserPreferences()
            }
        } else {
            UserPreferences()
        }
    }

    /**
     * Update preferences by applying a transform function.
     */
    suspend fun update(transform: (UserPreferences) -> UserPreferences) {
        context.dataStore.edit { prefs ->
            val current = prefs[PREFS_KEY]?.let {
                try { json.decodeFromString<UserPreferences>(it) } catch (_: Exception) { UserPreferences() }
            } ?: UserPreferences()
            val updated = transform(current)
            prefs[PREFS_KEY] = json.encodeToString(updated)
        }
    }

    // ── Convenience methods ──────────────────────────────────────────────────

    suspend fun setTheme(theme: ThemeMode) = update { it.copy(theme = theme) }

    suspend fun toggleFavourite(packageName: String) = update { prefs ->
        val favs = prefs.favouritePackages.toMutableList()
        if (packageName in favs) {
            favs.remove(packageName)
        } else {
            if (favs.size < 6) favs.add(packageName)
        }
        prefs.copy(favouritePackages = favs)
    }

    suspend fun hideApp(packageName: String) = update { prefs ->
        val hidden = prefs.hiddenPackages.toMutableList()
        if (packageName !in hidden) hidden.add(packageName)
        // Also remove from favourites if hidden
        val favs = prefs.favouritePackages.toMutableList()
        favs.remove(packageName)
        prefs.copy(hiddenPackages = hidden, favouritePackages = favs)
    }

    suspend fun unhideApp(packageName: String) = update { prefs ->
        val hidden = prefs.hiddenPackages.toMutableList()
        hidden.remove(packageName)
        prefs.copy(hiddenPackages = hidden)
    }

    suspend fun setShowSystemApps(show: Boolean) = update { it.copy(showSystemApps = show) }
    suspend fun setBlurIntensity(level: Int) = update { it.copy(blurIntensity = level.coerceIn(0, 2)) }
    suspend fun setShowWeather(show: Boolean) = update { it.copy(showWeather = show) }
    suspend fun setShowClock(show: Boolean) = update { it.copy(showClock = show) }
    suspend fun setWeatherLocation(location: String) = update { it.copy(weatherLocation = location) }
    suspend fun setWeatherCelsius(celsius: Boolean) = update { it.copy(weatherCelsius = celsius) }
    suspend fun setWeather12hr(use12hr: Boolean) = update { it.copy(weather12hr = use12hr) }
    suspend fun setWrapFocus(wrap: Boolean) = update { it.copy(wrapFocus = wrap) }

    suspend fun restoreDefaults() = update { UserPreferences() }
}
