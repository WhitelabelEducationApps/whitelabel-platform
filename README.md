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

## App Customization

Each app built on this platform must provide the following. The platform code resolves everything by **resource name at runtime** — no code changes needed in the platform module itself.

### Drawables (`res/drawable-nodpi/`)

| Resource name | Purpose | Required |
|---|---|---|
| `home_background` | Wallpaper shown at 18% opacity behind the home screen grid | No — if absent, no background is rendered |
| `<plant_name>` | Primary image for a catalog item (name is the plant/item name lowercased, spaces→underscores) | Per item |
| `<plant_name>_2` … `<plant_name>_6` | Additional images for the detail gallery | Optional |

### Feature flags (`AppConfig`)

Pass an `AppConfig` instance to `HomeScreen` to control which features are active for a given app:

```kotlin
AppConfig(
    enableMap              = true,   // Show map view toggle in drawer
    enableCategories       = true,   // Show category label on cards
    enableLocationFilter   = false,  // Show "location relevant items" toggle (default OFF)
)
```

`enableLocationFilter = true` adds a drawer toggle that requests location permission and filters the item list to species/items native to the user's geographic zone. The banner ("x/total items fit your location") only appears once zones have been determined.

### Location filter internals

Zone detection uses offline TDWG bounding boxes (`ZoneGeoMapper`). Items are matched against zones via their `author` field (comma-separated zone IDs, e.g. `zone_central_europe,zone_southern_europe`). No internet connection required.

## Dependencies

- Kotlin Multiplatform
- Jetpack Compose Multiplatform
- Coroutines & Flow
- Material Design 3

## License

Part of the Whitelabel Educational Apps ecosystem.
