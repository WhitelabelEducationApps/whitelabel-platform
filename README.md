# Whitelabel Platform

Reusable Kotlin Multiplatform (KMP) library providing the concrete data layer, complete UI, and platform services for catalog-based educational applications.

## What's Included

- **Data Layer** - SQLDelight schema (CatalogItem, Author), ItemRepository implementation, DatabaseDriverFactory
- **Full UI** - Home grid, detail screen, language picker, map view, navigation (AppNavigation)
- **WhitelabelActivity** - Abstract base Activity that handles locale changes and Koin injection
- **Theme and Styling** - Material Design 3 theming with per-item dynamic colors
- **Platform Services** - Wallpaper, image preloading, color extraction (Palette)
- **DI Modules** - commonModule, viewModelModule, platformModule (Koin 4.0)
- **Runtime String Lookup** - getStringResource() resolves Android string resources by name

## Used By

- [MuseumKMP](https://github.com/WhitelabelEducationApps/MuseumKMP) - UNESCO World Heritage Sites
- [HerbalRedo](https://github.com/WhitelabelEducationApps/HerbalRedo) - Medicinal Plants Encyclopedia

## Architecture

```n
platform/
src/commonMain/         # Shared KMP code
  kotlin/com/whitelabel/platform/
    di/             # Koin DI modules
    data/           # SQLDelight data sources, models, repository
    presentation/   # Compose UI screens, components, theme
    domain/         # Business logic
  resources/
src/androidMain/        # Android-specific (maps, wallpaper, color extraction)
src/iosMain/            # iOS stubs (ready)

```n
## App Customization

Each app provides:

1. **Database** - A pre-built SQLite DB matching the CatalogItem/Author schema
2. **AppConfig** - Feature flags (enableMap, enableCategories, enableLocationFilter)
3. **String resources** - Android strings looked up at runtime by getStringResource()
4. **Drawable resources** - Item images named by convention
5. **ItemGrouper<T>** (optional) - Custom grouping strategy override via Koin

## Dependencies

- Kotlin Multiplatform
- Jetpack Compose Multiplatform
- SQLDelight
- Coroutines and Flow
- Koin 4.0
- Coil (image loading)
- Material Design 3

## License

Part of the Whitelabel Educational Apps ecosystem.
