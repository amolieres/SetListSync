# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

SetListSync is a collaborative setlist manager for bands built with Kotlin Multiplatform Mobile (KMM). It targets Android, iOS, Desktop (JVM), and Web (WASM/JS). Currently in Phase 1 (local setlist manager). The backend (`server/` module using Ktor) is planned for Phase 2.

## Build Commands

**Android:**
```bash
./gradlew :composeApp:assembleDebug
./gradlew :composeApp:installDebug
```

**Desktop (JVM):**
```bash
./gradlew :composeApp:run
```

**Web:**
```bash
./gradlew :composeApp:wasmJsBrowserDevelopmentRun   # WASM (preferred)
./gradlew :composeApp:jsBrowserDevelopmentRun        # JS fallback
```

**iOS:** Open `iosApp/` in Xcode and run from there.

**Server:**
```bash
./gradlew :server:run
```

**Tests:**
```bash
./gradlew test                          # All tests
./gradlew :composeApp:testDebugUnitTest # Android unit tests
./gradlew :composeApp:jvmTest           # Desktop/JVM tests
```

**Note:** Two internal Artifactory repos (R2S organization) require `ARTIFACTORY_USER` and `ARTIFACTORY_PASSWORD` environment variables to be set.

## Module Structure

- **`composeApp/`** — Compose Multiplatform UI app (Android, iOS, Desktop, Web)
- **`shared/`** — Shared KMP library (Android, iOS, JVM, JS, WASM) — no UI
- **`server/`** — Ktor backend (JVM only, Phase 2)
- **`iosApp/`** — iOS Xcode wrapper

All app source code lives in `composeApp/src/commonMain/kotlin/`.

## Architecture

The app follows **Clean Architecture** with **MVVM** and unidirectional data flow, organized into layers:

### Layers (inside `composeApp/src/commonMain/kotlin/`)

```
core/
  domain/      → Entities, repository interfaces, use cases (pure Kotlin, no framework deps)
  data/        → Repository implementations, Room DAOs/entities, DataStore

feature/       → One package per feature (user, settings, main, ...)
  */presentation/  → ViewModel + UiState + UiEvent
  */ui/            → Composable screens

app/
  navigation/  → NavGraph + Destinations

common/
  di/          → Koin module definitions
  util/        → Platform helpers (Database, DataStore, Dispatchers)
```

### Key Patterns

**MVVM with Flows:**
- `ViewModel` exposes `StateFlow<UiState>` for screen state and `SharedFlow<UiEvent>` for one-time events (navigation, toasts).
- Composables collect state via `collectAsStateWithLifecycle()` and emit events back to the ViewModel.

**Repository Pattern:**
- `core/domain/*/repository/` defines interfaces.
- `core/data/*/` provides implementations.
- Use cases in `core/domain/*/usecase/` orchestrate repositories.

**Dependency Injection (Koin):**
Four modules in `common/di/`:
1. `platformModule` — platform-specific (Database, DataStore builders via `expect/actual`)
2. `repositoryModule` — binds repository interfaces to implementations
3. `useCaseModule` — use case instances
4. `viewModelModule` — ViewModels

**`expect/actual` for platform differences:**
Used for database initialization (`getDatabaseBuilder`), DataStore path, and coroutine dispatchers. Platform-specific implementations live in `androidMain/`, `iosMain/`, `jvmMain/`, etc.

### Persistence

- **Room** (`core/data/local/`) — local database for entities (Android, iOS, Desktop only — JS/WASM not supported). Schema exported to `composeApp/schemas/`.
- **DataStore** (`core/data/preferences/`) — stores user session (current user ID, password hash, notation preference FR/EN).

### Navigation

Compose Navigation with a sealed `Destinations` class in `app/navigation/`. The root `AppNavGraph` selects start destination based on DataStore session state.

## Dependency Versions

All versions are centralized in `gradle/libs.versions.toml`. Key versions:
- Kotlin: `2.2.21`
- Compose Multiplatform: `1.9.3`
- Room: `2.8.3` / DataStore: `1.1.7`
- Koin: `4.1.1`
- Ktor: `3.3.2`
- Android: compileSdk/targetSdk `36`, minSdk `24`

Room uses KSP for code generation. When adding a new `@Entity`, register it in the `@Database` annotation in `SetListSyncDatabase` and bump the database version.
