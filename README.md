# ClearTV — Android TV Launcher

A lightweight, ad-free home screen replacement for Amazon Fire Stick.
Built with Kotlin + Jetpack Compose + `androidx.tv`.

## Features

- 🏠 **Clean home screen** — gradient background, animated tiles, no ads
- ⭐ **Favourites row** — pin up to 6 apps (long-press to manage)
- 🎨 **Dark mode** — Light / Dark / System, instant toggle
- ⛅ **Weather widget** — Open-Meteo (no API key), 3-day forecast
- 🕐 **Clock widget** — blinking colon, day & date
- 📶 **Status bar** — real WiFi signal + device name
- 🌙 **Screensaver** — Dim, Clock, or Slideshow mode
- ⚙️ **Settings** — theme, blur, screensaver, weather location
- 🔄 **Live updates** — app grid refreshes on install/uninstall
- ♿ **Accessible** — TalkBack content descriptions on all tiles

## Build

Requires **Android Studio Iguana+** (bundled SDK 34).

```bash
# Debug build
./gradlew assembleDebug

# Release build (R8 minified)
./gradlew assembleRelease
```

## Install (ADB sideload)

```bash
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Set as Default Launcher

1. After install, press **Home** on the Fire Stick remote
2. Fire OS will ask which launcher to use — select **ClearTV**
3. Choose "Always"

## Revert to Stock Launcher

```bash
adb shell pm clear com.cleartv
```

Then press Home — Fire OS launcher will resume as default.

## Architecture

```
com.cleartv
├── data/
│   ├── AppRepository.kt          # PackageManager queries
│   ├── AppInstallReceiver.kt     # BroadcastReceiver for installs
│   ├── PreferencesRepo.kt        # DataStore (JSON)
│   ├── WeatherRepository.kt      # Ktor + Open-Meteo
│   └── model/
│       ├── AppInfo.kt
│       ├── UserPreferences.kt
│       └── WeatherData.kt
├── ui/
│   ├── home/
│   │   ├── HomeScreen.kt
│   │   ├── HomeViewModel.kt
│   │   ├── AppTile.kt
│   │   ├── AppsGrid.kt
│   │   ├── ContextMenu.kt
│   │   └── FavouritesRow.kt
│   ├── settings/
│   │   ├── SettingsScreen.kt
│   │   └── SettingsViewModel.kt
│   ├── screensaver/
│   │   ├── ScreensaverOverlay.kt
│   │   └── ScreensaverViewModel.kt
│   ├── widgets/
│   │   ├── ClockWidget.kt
│   │   ├── StatusWidget.kt
│   │   └── WeatherWidget.kt
│   └── theme/
│       ├── Colours.kt
│       ├── Theme.kt
│       └── Type.kt
├── util/
│   ├── FocusUtil.kt
│   └── IntentUtil.kt
└── MainActivity.kt
```

## License

Private — © 2026
