# ClearTV — Android TV Launcher

A lightweight, ad-free home screen replacement for Amazon Fire Stick.
Built with Kotlin + Jetpack Compose + `androidx.tv`.

## Features

- 🏠 **Clean home screen** — 160° gradient background, spring-animated tiles, decorative blobs
- ⭐ **Favourites row** — pin up to 6 apps (long-press to manage)
- 🎨 **Accent colour themes** — Blue, Green, Red, Orange, Pink, Purple
- 🌗 **Brightness modes** — Light, Dark, or **Auto** (switches at local sunrise/sunset)
- 🚀 **Launch overlay** — frosted glass overlay with app icon on open
- ⛅ **Weather widget** — Open-Meteo (no API key), 3-day forecast, frosted glass card
- 🕐 **Clock widget** — blinking colon, day & date
- 📶 **Status bar** — real WiFi signal + device name, frosted glass pill
- 🌙 **Screensaver** — Dim, Clock, or Slideshow mode
- ⚙️ **Settings** — brightness, accent colour, blur, screensaver, weather location
- 🔄 **Live updates** — app grid refreshes on install/uninstall
- ♿ **Accessible** — TalkBack content descriptions on all tiles

## Visual Design

### Frosted Glass
Weather, status, and widget cards use a layered frosted glass effect:
1. The screen background gradient is reconstructed and blurred (`Modifier.blur`)
2. A theme-adaptive semi-transparent overlay is applied on top
3. A white hairline border (80% opacity light / 20% dark) finishes the card

All layers use `ClearTVColors` tokens so they adapt correctly to light and dark modes.

### App Tiles
- **Resting**: subtle `0 2px 12px rgba(0,0,0,0.08)` drop shadow
- **Focused**: `0 12px 40px rgba(0,0,0,0.18)` shadow + outer accent-coloured ring drawn outside clip bounds
- **Spring animation**: `dampingRatio=0.65`, `stiffness=380` — ~7% overshoot on focus
- **Label bar**: fades in at tile bottom on focus (opacity 0→1, 150ms)

### Auto Brightness Mode
Uses the NOAA fractional-year solar position algorithm to compute local sunrise and sunset
from the user's geocoded weather location coordinates. Switches palettes at the exact minute.
Rechecks every 60 seconds via a `LaunchedEffect` ticker in `MainActivity`.

### Accent Colours
The selected accent drives `focusRing`, `blobBlue`, and `accent` tokens throughout the theme.
All six options work correctly in both light and dark palettes.

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
│       ├── UserPreferences.kt    # ThemeMode (AUTO), AccentColor, location coords
│       └── WeatherData.kt
├── ui/
│   ├── home/
│   │   ├── HomeScreen.kt         # 160° gradient, blob positions, launch overlay
│   │   ├── HomeViewModel.kt      # launchingApp state, geocode coord caching
│   │   ├── AppTile.kt            # Shadows, outer focus ring, label bar overlay
│   │   ├── AppsGrid.kt
│   │   ├── ContextMenu.kt
│   │   ├── FavouritesRow.kt
│   │   └── LaunchOverlay.kt      # Full-screen frosted launch animation
│   ├── settings/
│   │   ├── SettingsScreen.kt     # Brightness chips, accent colour swatches
│   │   └── SettingsViewModel.kt
│   ├── screensaver/
│   │   ├── ScreensaverOverlay.kt
│   │   └── ScreensaverViewModel.kt
│   ├── widgets/
│   │   ├── ClockWidget.kt
│   │   ├── StatusWidget.kt       # Frosted glass pill, theme-adaptive
│   │   └── WeatherWidget.kt      # Frosted glass card, theme-adaptive
│   ├── util/
│   │   └── DrawModifiers.kt      # softDropShadow, tileRestingShadow,
│   │                             # tileFocusedShadow, outerFocusRing
│   └── theme/
│       ├── Colours.kt            # Light/Dark palettes + accent color helpers
│       ├── Theme.kt              # ClearTVTheme(darkTheme, accentColor)
│       └── Type.kt
├── util/
│   ├── FocusUtil.kt
│   ├── IntentUtil.kt
│   └── SunriseSunset.kt         # NOAA sunrise/sunset algorithm
└── MainActivity.kt               # AUTO mode ticker, accent color wiring
```

## Settings Reference

| Setting | Options | Notes |
|---|---|---|
| Brightness | Light / Dark / **Auto** | Auto switches at local sunrise/sunset |
| Accent Colour | Blue / Green / Red / Orange / Pink / Purple | Affects focus ring, links, blobs |
| Blur Intensity | Low / Medium / High | Controls frosted glass blur radius |
| Show Clock | On / Off | |
| Show Weather Widget | On / Off | |
| Screensaver | Dim / Clock / Slideshow | Idle timeout: 5–60 min |
| Weather Location | City name | Empty = London default |
| Units | °C / °F | |
| Time Format | 12-hour / 24-hour | |

## License

Private — © 2026
