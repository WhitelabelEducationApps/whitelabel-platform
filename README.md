# Whitelabel Platform

Reusable Kotlin Multiplatform (KMP) library providing core components, utilities, and abstractions for building heritage site applications.

## What's Included

- **Localization & Language Management** — Multi-language support for 10+ languages
- **Generic UI Components** — Reusable Compose Multiplatform components
- **Theme & Styling** — Consistent Material Design 3 theming
- **Platform Services** — Wallpaper, image preloading, color extraction
- **Utilities** — Logging, string manipulation, color utilities, context helpers

## Used By

- [MuseumKMP](https://github.com/whitelabel-educational-apps/MuseumKMP) — UNESCO World Heritage Sites application

## Architecture

```
platform/
├── src/commonMain/         # Shared KMP code
│   ├── kotlin/com/whitelabel/platform/
│   │   ├── di/             # Dependency injection
│   │   ├── presentation/   # UI components & screens
│   │   ├── domain/         # Business logic abstractions
│   │   └── utils/          # Utilities
│   └── resources/
├── src/androidMain/        # Android-specific implementations
└── src/iosMain/           # iOS-specific implementations (ready)
```

## Dependencies

- Kotlin Multiplatform
- Jetpack Compose Multiplatform
- Coroutines & Flow
- Material Design 3

## License

Part of the Whitelabel Educational Apps ecosystem.
