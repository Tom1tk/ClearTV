# ClearTV

A clean, ad-free, lightweight Android TV home screen replacement.

**Target device:** Amazon Fire Stick 4K MAX (1st gen) · Fire OS 8 (Android 11 base)

## Design Philosophy

The launcher should disappear. It should feel like the TV turned on and your apps are just there. Inspired by iOS 26's liquid glass aesthetic — light, frosted, breathing. No visual noise, no dark patterns, no content you didn't choose.

## Build

```bash
./gradlew assembleDebug
```

## Install (ADB sideload)

```bash
# Enable developer mode: Settings → My Fire TV → About → click "Build" 7 times
adb connect <fire_stick_ip>:5555
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

## Set as Default Launcher

On the Fire Stick, press Home → select ClearTV → "Always"

Or via ADB:
```bash
adb shell cmd package set-home-activity com.cleartv/.MainActivity
```

## Revert to Fire OS Launcher

```bash
adb shell cmd package set-home-activity com.amazon.firelauncher/.Launcher
```

## Tech Stack

- **Kotlin** + **Jetpack Compose** + **androidx.tv**
- **Coil** for image loading
- **Coroutines** for async
- Min SDK 28 · Target SDK 34

## License

Private — not for redistribution.
