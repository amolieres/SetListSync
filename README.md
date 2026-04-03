# SetListSync

**A collaborative setlist manager for bands.**  
Create your repertoire, build setlists for gigs or rehearsals, and sync the current song live with your band members.

---

## 🎯 Why SetListSync?

Managing setlists in a band often means juggling spreadsheets, PDFs, chat messages, and last-minute changes during rehearsals or gigs.  
**SetListSync centralizes everything in one app and keeps all band members synchronized in real-time.**

---

## 🚀 MVP Roadmap

### Phase 1 – Local SetList Manager (Offline)

**Legend:** 🟢 Done &nbsp;|&nbsp; 🟠 In progress &nbsp;|&nbsp; ⚪️ Not started

#### Features
- 🟢 Manage **user** CRUD
- 🟢 Manage **band** (create, list, detail, edit, delete)
  - Multi-step creation screen (name + genres → social URLs → members)
  - Band info: name, email, Instagram / Facebook / TikTok URLs, musical genres
  - Dedicated edit screen (`BandEditScreen`) separate from detail view
- 🟢 Manage **band members** (add, edit, delete — with roles: bass, guitar, vocals, drums…)
- 🟢 Manage **settings** (DataStore session wired, settings screen UI in progress)
- 🟢 Manage **songs** (per band)
  - Fields: title, original artist, duration (min/sec), key/tonality, BPM
  - `SongKey` enum — 30 standard keys (15 major + 15 minor, circle of fifths)
  - Tonality display respects the user's **notation preference** (French: Do/Ré/Mi… or English: C/D/E…)
  - **Deezer search** — search by title/artist and pre-fill all fields
  - **GetSongBPM auto-fill** — BPM and key enriched automatically after selection
  - Song count shown live on each band card (reactive flow)
- ⚪️ Manage **gigs & setlists**
  - song order
  - compute total duration
- ⚪️ Add **member-specific song notes**
  - e.g. lyrics for singer, pedal settings for guitarist, first note for bassist
- ⚪️ **Export setlists** as PDF or image for sharing

#### Technical Stack
- 🟢 Local database — Room (Android, iOS, Desktop) — v3 (migration v2→v3 applied)
- 🟢 User session — DataStore (current user ID, password hash, notation preference FR/EN)
- 🟢 Reactive architecture — Kotlin Flows throughout (Room → Repository → UseCase → ViewModel → UI)
- 🟢 HTTP client — Ktor (OkHttp/Android, Darwin/iOS, Java/Desktop) with ContentNegotiation + Kotlinx Serialization
- 🟢 ViewModel unit tests — `BandDetailViewModelTest`, `BandEditViewModelTest` (commonTest)
- 🟢 Internationalization (FR/EN) — all strings centralized in `composeResources/values/strings.xml`, French translation in `values-fr/strings.xml`
- 🟢 App theme — custom Material 3 color scheme & typography + extractable design system (`app/designsystem/`)
- 🟢 App icon — launcher icon for Android, iOS, Desktop
- ⚪️ PDF exporter
- ⚪️ Printer management


### 🔜 Phase 2 – Live Sync Mode (Online)
Add collaboration & real-time sync:
- **API + database + hosting**
- Enable **cloud sync** of users, bands, songs, notes, setlists
- “**Show Time**” mode
  - master device controls current song
  - follower devices display the song and their notes
- Realtime set progression sync  
  (e.g. Bluetooth LE or network-based shared session)

---

### 🎯 Phase 3 – AI-assisted SetList Builder
Make planning smarter:
- Extend gig details:  
  - venue, music style, required duration, audience type
- Use this data to **propose optimized setlists**
- Optional AI suggestions based on song energy, key transitions, variety, etc.

---

## 🧠 Future Ideas (Nice to Have)
- Sections inside a setlist (e.g. Acoustic / Electric / Encore)
- Track per-song performance history
- Metronome / tempo display
- Scrollable lyrics or chords
- Integrations (Spotify, YouTube, MIDI, pedals, DMX lighting…)
- Export to other formats (CSV, JSON, Setlist.fm, etc.)

---

## 🛠 Technical Stack

**Core Architecture**
- **Kotlin Multiplatform (KMP)**
  - Shared business logic across all platforms

**UI Layer**
- **Compose Multiplatform**
  - Single UI codebase for Android, iOS, and Desktop
  - Faster development and consistent design across devices
  - Mobile-first (on-stage usage), Desktop for preparation

**Backend (planned in Phase 2)**
- REST or GraphQL APIs
- Lightweight cloud hosting / database (to be decided)

**Realtime Sync**
- Initial experiments with **Bluetooth LE** or local network
- Possible **WebSocket-based** sync for online mode

---

## ✅ Current Status

**Phase 1 — In development**

| Area | Status | Notes |
|------|--------|-------|
| Architecture (Clean Arch + MVVM + Koin DI) | 🟢 Done | All layers in place |
| Room database (entities, DAOs, migrations) | 🟢 Done | v3 — Band, BandMember, User, Song, Gig, SetList entities |
| DataStore (user session) | 🟢 Done | userId, password hash, notation preference |
| User feature | 🟢 Done | Full CRUD |
| Band feature — create | 🟢 Done | 3-step screen: name + genres, social URLs, members |
| Band feature — detail & delete | 🟢 Done | Info header (email, genres, social), delete with confirm dialog |
| Band feature — edit | 🟢 Done | Dedicated `BandEditScreen` / `BandEditViewModel` |
| Band members feature | 🟢 Done | Add, edit, delete with roles (guitar, bass, vocals, drums…) |
| Navigation (Compose Navigation) | 🟢 Done | All band screens wired: Main → Creation, Detail, Edit, Members |
| ViewModel unit tests | 🟢 Done | `BandDetailViewModelTest`, `BandEditViewModelTest` |
| i18n (FR/EN) | 🟢 Done | All strings in `composeResources/values/strings.xml` (EN) + `values-fr/strings.xml` (FR) |
| Settings feature | 🟢 Done | DataStore wired, screen UI pending |
| App theme (Material 3) | 🟢 Done | Custom color scheme & typography + design system in `app/designsystem/` (dimensions, reusable components) |
| App icon | 🟢 Done | Launcher icon for Android, iOS, Desktop |
| Song feature | 🟢 Done | Full CRUD per band — `BandSongsScreen` (list with `SongItem` cards), `BandSongDetailScreen` (add/edit), Deezer search, GetSongBPM auto-fill, `SongKey` enum (30 keys, FR/EN display via `NoteNotation`), song count in band list |
| Gig & SetList feature | ⚪️ Not started | Domain + data layer scaffolded, no UI yet |
| PDF export | ⚪️ Not started | — |

---

## 🤝 Contributions
The project is currently in early stages and not open to external contributions yet.  
Once the core structure is stable, contribution guidelines will be added.

---

## 📄 License
TBD (MIT or Apache 2.0 likely)

---

## 🎵 About
Built by musicians, for musicians.  
Because every band deserves clean setlists and perfect sync on stage.

---

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

### Build and Run Server

To build and run the development version of the server, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :server:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :server:run
  ```

### Build and Run Web Application

To build and run the development version of the web app, use the run configuration from the run widget
in your IDE's toolbar or run it directly from the terminal:
- for the Wasm target (faster, modern browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:wasmJsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:wasmJsBrowserDevelopmentRun
    ```
- for the JS target (slower, supports older browsers):
  - on macOS/Linux
    ```shell
    ./gradlew :composeApp:jsBrowserDevelopmentRun
    ```
  - on Windows
    ```shell
    .\gradlew.bat :composeApp:jsBrowserDevelopmentRun
    ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---
