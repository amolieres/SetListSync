# SetListSync

**A collaborative setlist manager for bands.**  
Create your repertoire, build setlists for gigs or rehearsals, and sync the current song live with your band members.

---

## 🎯 Why SetListSync?

Managing setlists in a band often means juggling spreadsheets, PDFs, chat messages, and last-minute changes during rehearsals or gigs.  
**SetListSync centralizes everything in one app and keeps all band members synchronized in real-time.**

---

## 🚀 MVP Roadmap

### ✅ Phase 1 – Local SetList Manager (Offline)
Core features:
- 🟢 Manage **user** CRUD 
- ⚪️ Manage my **band**
- ⚪️ Manage **settings**  
- ⚪️ Manage **band members** (with roles: bass, guitar, vocals, drums…)
- ⚪️ Manage **songs**  
  - title, duration, key/tone, external links, etc.  
  - stored in a **global song library**
- ⚪️ Add **member-specific song notes**  
  - e.g. lyrics for singer, pedal settings for guitarist, first note for bassist
- ⚪️ Manage **gigs & setlists**
  - song order
  - compute total duration
- ⚪️ **Export setlists** as PDF or image for sharing

---

Technical Stack: 
- 🟢 Local database >> ROOM
- ⚪️ User DataStore ( https://developer.android.com/kotlin/multiplatform/datastore?hl=fr)
- ⚪️ Printer management 
- ⚪️ Internationalization (FR/EN)
- ⚪️ PDF exporter


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
🛠 In development – focusing on **Phase 1 (Local SetList Manager)**

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
