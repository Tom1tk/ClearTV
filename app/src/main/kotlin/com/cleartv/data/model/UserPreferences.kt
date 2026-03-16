package com.cleartv.data.model

import kotlinx.serialization.Serializable

/**
 * User preferences — persisted via DataStore as JSON.
 * Maps to the proto schema in the spec (§4.8) but uses
 * kotlinx.serialization instead of protobuf for simplicity.
 */
@Serializable
data class UserPreferences(
    val theme: ThemeMode = ThemeMode.AUTO,
    val accentColor: AccentColor = AccentColor.BLUE,
    val screensaverEnabled: Boolean = true,
    val screensaverTimeoutMin: Int = 10,
    val screensaverType: ScreensaverType = ScreensaverType.DIM,
    val showWeather: Boolean = true,
    val showClock: Boolean = true,
    val weatherLocation: String = "",        // empty = IP fallback
    val weatherCelsius: Boolean = true,
    val weather12hr: Boolean = true,
    val blurIntensity: Int = 1,              // 0=low, 1=med, 2=high
    val favouritePackages: List<String> = emptyList(),
    val hiddenPackages: List<String> = emptyList(),
    val wallpaperPath: String = "",
    val wallpaperColour: String = "",
    val showSystemApps: Boolean = false,
    val wrapFocus: Boolean = false,
    // Cached coordinates from geocoding — used for Auto sunrise/sunset mode
    val locationLat: Double = 51.5074,       // default: London
    val locationLon: Double = -0.1278,
)

@Serializable
enum class ThemeMode {
    LIGHT,
    DARK,
    /** Switches between light and dark based on local sunrise/sunset. */
    AUTO,
}

@Serializable
enum class AccentColor {
    BLUE,
    GREEN,
    RED,
    ORANGE,
    PINK,
    PURPLE,
}

@Serializable
enum class ScreensaverType {
    DIM,
    CLOCK,
    SLIDESHOW,
}
