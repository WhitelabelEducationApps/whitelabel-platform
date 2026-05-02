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

## Color extraction

Each item card displays colors derived from its primary image (vibrant, muted, dark/light variants — matching Android Palette swatches). Colors are resolved in this priority order at runtime:

1. **Session cache** (`HashMap<Long, ExtractedColors>`) — populated on first access, reused for the rest of the session. Scroll-back is instant.
2. **Precomputed asset** (`assets/extracted_colors.json`) — generated at build time by `scripts/extract_colors.py`. The JSON is keyed by drawable resource entry name (e.g. `"acaipalm"`). For release builds this covers all known plants so no Palette work happens at runtime.
3. **Runtime Palette extraction** — fallback for items absent from the JSON (new plants added since last release, debug builds). Runs once on `Dispatchers.Default` and is then cached.

Logcat tag: `ColorExtraction`

```
D ColorExtraction  Loaded 501 pre-extracted color entries from assets
D ColorExtraction  [acaipalm] colors loaded from precomputed asset cache
D ColorExtraction  [newplant] not in precomputed cache — running Palette extraction
D ColorExtraction  [newplant] Palette extraction complete
```

### Regenerating the asset

```bash
# requires: pip install Pillow
python scripts/extract_colors.py \
  --drawable-dir androidApp/src/main/res/drawable-nodpi \
  --output androidApp/src/main/assets/extracted_colors.json
```

The Gradle task `extractColors` runs this automatically before every `assembleRelease` / `bundleRelease`. It is skipped by Gradle's up-to-date check if no drawables changed since the last run.

Only **base images** (no `_N` suffix, e.g. `acaipalm.webp` not `acaipalm_2.webp`) are processed — one entry per plant, matching the image used for card thumbnails.

## Dependencies

- Kotlin Multiplatform
- Jetpack Compose Multiplatform
- Coroutines & Flow
- Material Design 3

## License

Part of the Whitelabel Educational Apps ecosystem.
