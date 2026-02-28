# ClearTV — Agent Handoff Notes
**Date**: 2026-02-28  
**Conversation ID**: `1406a04a-aa0e-4900-a6d0-08b8cc2d08f4`  
**GitHub**: `https://github.com/Tom1tk/ClearTV.git` — branch `main`

---

## What Was Built

A complete Android TV launcher app to replace the Fire Stick home screen. Written in Kotlin + Jetpack Compose. **All 4 planned phases are implemented and committed.**

### Phases Complete
| Phase | Commit | Summary |
|---|---|---|
| 1 — Foundation | `7d8d0c5` | Project scaffold, manifest, theme, home screen, clock, grid, tiles |
| 2 — Personalisation | `d4b34b7` | DataStore preferences, long-press context menu, Settings screen, dark mode |
| 3 — Widgets | `1122d2f` | Weather widget (Open-Meteo), Status widget, Screensaver (Dim/Clock/Slideshow) |
| 4 — Hardening | `ca315fb` | AppInstallReceiver, no-network weather fallback, accessibility, ProGuard, v1.0.0 |
| **Bug fixes** | `66edc28` `39865fd` `958f46d` | JDK fix, manifest fix, **crash fix** (see below) |

**Latest commit**: `958f46d`

---

## Current State — What Was Happening When We Stopped

The app was crashing on launch in an Android TV emulator (API 36, arm64, 1080p). We:
1. Debugged via ADB (`/Users/tom7/Library/Android/sdk/platform-tools/adb`)
2. Found the crash in logcat: **Compose `StackOverflowError`** during measure pass
3. Fixed and rebuilt. App now launches successfully (confirmed via `adb logcat`)

**The fix is committed and pushed (`958f46d`).** The emulator showed zero crashes after the fix.

### The Bug That Was Fixed
`LazyVerticalGrid` inside `Column + verticalScroll()` — illegal in Compose (lazy layouts can't be inside bounded scroll containers). Fixed by:
- `HomeScreen.kt` now uses a single `LazyColumn` as the root scroll container
- Apps grid uses `FlowRow` (non-lazy, 6-column) as an item inside the `LazyColumn`

---

## Known Remaining Issues / Next Steps

### Immediate
- [ ] **Actually test the emulator visually** — the app launches without crashing, but the user hadn't confirmed the UI was rendering correctly when we ran out of context. Open Android Studio, Run the app, confirm the home screen renders.
- [ ] **SettingsScreen also uses `Column + verticalScroll`** — this is fine since it has no lazy children, but worth verifying.

### Warnings from last build (non-fatal, but clean up when convenient)
- `Variable 'hueShift' is never used` — in `ScreensaverOverlay.kt` (Slideshow stub)
- `calculateSignalLevel is deprecated` — in `StatusWidget.kt`, use `WifiManager.calculateSignalLevel(rssi)` (no second param) on API 30+
- `Parameter 'useCelsius' is never used` — in `WeatherWidget.kt` (the widget receives it but passes formatting to ViewModel)

### Phase 5 ideas (not started)
- Weather location picker (text input dialog for city name — currently hardcoded fallback to London)
- Slideshow screensaver (file picker for user photos)
- Wallpaper support (colour picker or image)
- App reordering (drag-and-drop on grid)
- Fire Stick as default launcher (works on actual Fire Stick via `CATEGORY_HOME`; emulator requires going to Settings → Apps → Default Apps)

---

## Build Instructions

```bash
cd /Users/tom7/Documents/ClearTV

# Gradle uses Android Studio's JDK 21 (set in gradle.properties)
./gradlew assembleDebug

# Or build + install in one step:
./gradlew installDebug

# Install manually via ADB
export PATH="$PATH:/Users/tom7/Library/Android/sdk/platform-tools"
adb install -r app/build/outputs/apk/debug/app-debug.apk

# Launch on emulator
adb -s emulator-5554 shell am start -n com.cleartv/.MainActivity

# Read crash logs
adb -s emulator-5554 logcat -d | grep -iE "AndroidRuntime|FATAL|cleartv"
```

---

## Key Files

```
app/src/main/
├── kotlin/com/cleartv/
│   ├── MainActivity.kt                     ← NavHost + screensaver + BroadcastReceiver
│   ├── data/
│   │   ├── AppRepository.kt                ← PackageManager queries
│   │   ├── AppInstallReceiver.kt           ← Live app install/uninstall detection
│   │   ├── PreferencesRepo.kt              ← DataStore (JSON, all settings)
│   │   ├── WeatherRepository.kt            ← Ktor + Open-Meteo API (no key needed)
│   │   └── model/
│   │       ├── AppInfo.kt
│   │       ├── UserPreferences.kt          ← All settings (theme, screensaver, weather, etc.)
│   │       └── WeatherData.kt
│   ├── ui/home/
│   │   ├── HomeScreen.kt                   ← LazyColumn root, FlowRow grid (crash-fixed)
│   │   ├── HomeViewModel.kt                ← App list, favourites, weather, context menu
│   │   ├── AppTile.kt                      ← Frosted glass tile with spring anim + long-press
│   │   ├── ContextMenu.kt                  ← Pin/Unpin/Hide overlay
│   │   └── FavouritesRow.kt
│   ├── ui/settings/
│   │   ├── SettingsScreen.kt               ← Full settings panel
│   │   └── SettingsViewModel.kt
│   ├── ui/screensaver/
│   │   ├── ScreensaverOverlay.kt           ← Dim / Clock (drifting) / Slideshow
│   │   └── ScreensaverViewModel.kt         ← Idle timer
│   ├── ui/widgets/
│   │   ├── ClockWidget.kt                  ← HH:MM blinking colon
│   │   ├── WeatherWidget.kt                ← Current + 3-day forecast
│   │   └── StatusWidget.kt                 ← Real WiFi signal strength
│   └── ui/theme/
│       ├── Colours.kt                      ← Light + Dark palettes
│       ├── Theme.kt                        ← ClearTVTheme + CompositionLocal
│       └── Type.kt                         ← All text styles
├── res/
│   ├── values/strings.xml
│   ├── values/colors.xml
│   ├── drawable/banner.xml                 ← TV banner placeholder
│   └── mipmap-anydpi-v26/ic_launcher.xml
└── AndroidManifest.xml                     ← LAUNCHER + HOME intent filters

gradle.properties                           ← org.gradle.java.home = Android Studio JDK 21
app/build.gradle.kts                        ← AGP 8.5, Compose BOM, Ktor, DataStore
```

---

## Important Context

- **Target device**: Amazon Fire Stick 4K MAX (Fire OS 8, Android 9)
- **Test emulator**: Android TV, API 36, arm64, 1080p (in Android Studio)
- **ADB path**: `/Users/tom7/Library/Android/sdk/platform-tools/adb`
- **JDK**: Android Studio's bundled JDK 21 (`/Applications/Android Studio.app/Contents/jbr/Contents/Home`)
- Files excluded from git: `ClearTV_Launcher_Spec.md`, `tv-launcher-light.jsx` (in `.gitignore`)
