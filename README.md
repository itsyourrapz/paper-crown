# Paper Crown

A local-first desktop roguelike game based on Rock-Paper-Scissors (Batu, Gunting, Kertas). Fight a random bot opponent through multiple runs, collect buffs, survive HP-based progression, unlock achievements, and build persistent statistics.

This project demonstrates core OOP concepts — **inheritance**, **polymorphism**, **encapsulation**, **abstraction**, **error handling**, and **JavaFX GUI** — using a Java 21 multi-module Gradle architecture.

## Architecture

```text
JavaFX Desktop  ──REST──>  Spring Boot Backend  ──>  PostgreSQL
    (MVVM)                     (Service Layer)         (via Flyway)
```

## Tech Stack

| Layer | Technology |
|-------|------------|
| Desktop Client | JavaFX 23, Ikonli, JFreeChart |
| Backend Service | Spring Boot 3.4.3, JPA/Hibernate |
| Database | PostgreSQL 16 via Docker |
| Shared Contracts | Multi-module Gradle project |
| Testing | JUnit 5, Mockito |

> Runtime note: project ini bisa dijalankan dengan Java 21. Source/bytecode tetap ditargetkan ke Java 21 agar kompatibel dengan Spring Boot 3.4.3.

## Prerequisites

Pastikan sudah terpasang:

- JDK 21
- Gradle
- Docker dan Docker Compose
- Git

Cek versi Java dan Gradle:

```bash
java --version
gradle --version
```

> Project ini tetap menyediakan Gradle Wrapper (`./gradlew`). Command di bawah memakai wrapper agar versi Gradle yang digunakan konsisten dengan project.

## Quick Start

Jalankan semua command dari root repository `paper-crown/`.

### 1. Prepare Gradle Wrapper

Jalankan perintah Gradle Wrapper terlebih dahulu, lalu pastikan file `gradlew` bisa dieksekusi:

```bash
chmod +x gradlew
./gradlew --version
```

### 2. Start PostgreSQL

```bash
docker compose -f docker/docker-compose.yml up -d
```

Cek database sudah hidup:

```bash
docker compose -f docker/docker-compose.yml ps
```

### 3. Start Backend

Buka terminal pertama:

```bash
./gradlew :backend-service:bootRun
```

Backend berjalan di:

```text
http://localhost:8080
```

### 4. Launch Desktop Client

Buka terminal kedua, lalu jalankan:

```bash
./gradlew :desktop-client:run
```

Pastikan backend di terminal pertama tetap berjalan saat desktop client dibuka.

## Build & Test

```bash
./gradlew build                       # Compile all modules + run tests
./gradlew test                        # Run all tests
./gradlew :backend-service:bootRun    # Start backend API
./gradlew :desktop-client:run         # Launch JavaFX desktop client
```

## OOP Concepts Demonstrated

### 1. Inheritance

All JavaFX views extend built-in layout classes, inheriting their rendering and behavior:

| Class | Parent | File |
|-------|--------|------|
| `PlayView`, `DashboardView`, `HistoryView`, `AchievementsView`, `SettingsView` | `VBox` | `desktop-client/.../view/*.java` |
| `MainView` | `BorderPane` | `desktop-client/.../view/MainView.java` |
| `SidebarItem` | `HBox` | `desktop-client/.../view/SidebarItem.java` |
| `StatCard`, `RunCard`, `AchievementCard`, `BuffCard` | `VBox` | `desktop-client/.../component/*.java` |
| `ChartContainer` | `StackPane` | `desktop-client/.../component/ChartContainer.java` |
| `PaperCrownApp` | `Application` | `desktop-client/.../PaperCrownApp.java` |

Repository interfaces also inherit from `JpaRepository<T, ID>`, gaining CRUD operations for free.

### 2. Polymorphism

- **Runtime polymorphism via `switch` on enums** — `PlayView.showResult()` (`PlayView.java:177-198`) handles WIN/LOSS/DRAW with different animations; `RunService.submitMove()` (`RunService.java:93-115`) processes each outcome differently
- **Polymorphic theming via `instanceof`** — `ChartContainer.applyTheme()` (`ChartContainer.java:52-82`) handles `CategoryPlot`, `XYPlot`, and `PiePlot` with distinct styling
- **Method overriding** — `PaperCrownApp.start()` (`PaperCrownApp.java:14`) overrides `Application.start()` to set up the stage

### 3. Encapsulation

- **Private fields with public accessors** — All DTOs and JPA entities use `private` fields exposed through getters/setters (e.g., `RunEntity.java:12-93`, `MoveRequest.java:7-17`, `MoveResponse.java:8-35`)
- **Hidden implementation details** — `BackendClient` (`BackendClient.java:18-172`) encapsulates HTTP client, JSON serialization, and connection logic behind a clean API (`startRun()`, `submitMove()`, `getStats()`)
- **ViewModel hides threading** — `PlayViewModel` (`PlayViewModel.java:18-125`) runs HTTP calls on a private executor, updates JavaFX properties on the UI thread — callers never see threads or HTTP

### 4. Abstraction

- **Game rules abstracted** — `GameEngine` (`GameEngine.java:11-36`) hides the RPS resolution logic behind `resolve(Move, Move)` and `randomBotMove()` — services use it without knowing the win map or random implementation
- **Data access abstracted** — `RunRepository` (`RunRepository.java:12`) declares `findTopByStatusOrderByCreatedAtDesc(RunStatus)` — Spring Data generates the SQL automatically
- **Entity-DTO mapping abstracted** — `EntityMapper` (`EntityMapper.java:11-106`) provides `toRunDTO()`, `toRoundDTO()`, etc. Services call it without knowing mapping details

### 5. Error Handling & Exceptions

**Backend (service layer throws, handler catches):**

- `RunService.java:52` — `throw new IllegalStateException("An unfinished run already exists")` — prevents duplicate concurrent runs
- `RunService.java:70,82,143-147` — `.orElseThrow(() -> new NoSuchElementException(...))` — entity-not-found errors
- `RunService.java:85` — `throw new IllegalStateException("Run is already completed")` — invalid state for move submission
- `GlobalExceptionHandler.java:14-36` — `@ControllerAdvice` maps exceptions to HTTP status codes (404, 409, 400, 500) — centralized error handling, no try/catch in controllers

**Desktop (graceful degradation):**

- `PlayViewModel.java:64-65` — catches network errors and sets an observable `error` flag for the UI to display
- `PlayViewModel.java:53,81` — silently catches non-critical failures
- `PlayViewModel.java:66-68` — `finally` block always resets `loading` state
- `BackendClient.java:110-112,148,166-168` — wraps HTTP failures in `RuntimeException` with descriptive messages
- `BackendClient.java:33-41` — graceful degradation: `isHealthy()` returns `false` instead of crashing

### 6. JavaFX GUI (MVVM Pattern)

The user interface is built programmatically (no FXML) using the **MVVM (Model-View-ViewModel)** pattern:

| Layer | Role | Key Classes |
|-------|------|-------------|
| **View** | Builds layout, binds to observable properties, handles animations | `MainView`, `PlayView`, `DashboardView`, `HistoryView`, `AchievementsView`, `SettingsView` |
| **ViewModel** | Exposes observable state, abstracts backend calls, manages async operations | `PlayViewModel`, `DashboardViewModel`, `HistoryViewModel`, `AchievementsViewModel`, `SettingsViewModel` |
| **Model** | Backend REST API | `BackendClient`, Spring Boot services |

**Reusable components** (`desktop-client/.../component/`):
- `StatCard` — stats display with pseudo-class accent colors
- `RunCard` — collapsible run entry with round details
- `AchievementCard` — three visual states (unlocked, in-progress, locked)
- `BuffCard` — buff selection card with hover animation
- `ChartContainer` — JFreeChart wrapper with SwingNode and dark theming
- `Toast` — animated notification sliding in from the right

**Animations** — Scalable win effect, shake on loss, fade transitions between pages, staggered card entrance.

**Styling** — 491-line dark fantasy theme (`main.css`) with root CSS variables, pseudo-classes, and hover states.

## Game Rules

- You choose **Rock**, **Paper**, or **Scissors** each round
- Bot chooses randomly
- **Win** → survive the round
- **Loss** → lose 1 HP
- **Draw** → no HP loss
- **0 HP** → run ends

### Buffs

Every few rounds, choose from 3 random buffs:

| Type | Examples |
|------|----------|
| Survival | +1 Max HP, Heal 1 HP, Shield |
| Scoring | Double reward, Bonus streak points |
| Utility | Reroll token, Draw counts as win, Ignore loss |

### Achievements

11 achievements across 5 criteria types — milestones auto-unlock as you play.

## Project Structure

```text
paper-crown/
├── desktop-client/                   # JavaFX desktop application
│   └── src/main/java/com/papercrown/desktop/
│       ├── component/                # Reusable UI components
│       │   ├── StatCard.java         #   Stats display card
│       │   ├── RunCard.java          #   Collapsible run entry
│       │   ├── AchievementCard.java  #   Achievement tile
│       │   ├── BuffCard.java         #   Buff selection card
│       │   ├── ChartContainer.java   #   JFreeChart wrapper
│       │   └── Toast.java            #   Animated notification
│       ├── service/                  # Backend HTTP client
│       │   └── BackendClient.java    #   REST API access layer
│       ├── util/                     # Audio manager
│       │   └── AudioManager.java     #   Sound playback
│       ├── view/                     # JavaFX views (MVVM)
│       │   ├── MainView.java         #   Root navigation shell
│       │   ├── SidebarItem.java      #   Sidebar nav button
│       │   ├── PlayView.java         #   Game play screen
│       │   ├── DashboardView.java    #   Stats overview
│       │   ├── HistoryView.java      #   Run history
│       │   ├── AchievementsView.java #   Achievement gallery
│       │   └── SettingsView.java     #   Settings page
│       ├── viewmodel/                # ViewModel layer
│       │   ├── PlayViewModel.java    #   Game state & actions
│       │   ├── DashboardViewModel.java
│       │   ├── HistoryViewModel.java
│       │   ├── AchievementsViewModel.java
│       │   └── SettingsViewModel.java
│       └── PaperCrownApp.java        # JavaFX entry point
├── backend-service/                  # Spring Boot REST API
│   └── src/main/java/com/papercrown/backend/
│       ├── config/                   # CORS configuration
│       ├── controller/               # REST controllers
│       │   ├── RunController.java
│       │   ├── StatsController.java
│       │   ├── AchievementController.java
│       │   └── SettingsController.java
│       ├── entity/                   # JPA entities
│       │   ├── RunEntity.java
│       │   ├── RoundEntity.java
│       │   ├── BuffEntity.java
│       │   ├── RunBuffEntity.java
│       │   ├── AchievementEntity.java
│       │   └── SettingEntity.java
│       ├── exception/                # Error handling
│       │   └── GlobalExceptionHandler.java
│       ├── mapper/                   # Entity-DTO mapping
│       │   └── EntityMapper.java
│       ├── repository/               # JPA repositories
│       │   ├── RunRepository.java
│       │   ├── RoundRepository.java
│       │   ├── BuffRepository.java
│       │   └── ...
│       └── service/                  # Business logic
│           ├── GameEngine.java       #   RPS resolution
│           ├── RunService.java        #   Run lifecycle
│           ├── BuffService.java       #   Buff effects
│           ├── StatsService.java      #   Statistics
│           ├── AchievementService.java#   Achievements
│           └── SettingsService.java   #   Settings
├── shared/                           # Shared DTOs and enums
│   └── src/main/java/com/papercrown/shared/
│       ├── dto/                      # Data transfer objects
│       │   ├── MoveRequest.java
│       │   ├── MoveResponse.java
│       │   ├── RunDTO.java
│       │   ├── RoundDTO.java
│       │   ├── StatsDTO.java
│       │   ├── AchievementDTO.java
│       │   ├── BuffDTO.java
│       │   └── SettingDTO.java
│       └── enums/                    # Shared enumerations
│           ├── Move.java
│           ├── RoundOutcome.java
│           ├── RunStatus.java
│           └── BuffType.java
├── docker/                           # Docker Compose for PostgreSQL
├── infra/                            # Setup scripts
├── DESIGN.md                         # Design documentation
├── PRODUCT.md                        # Product context
├── TODO.md                           # Roadmap
└── AGENTS.md                         # Agent guidelines
```

## Settings

- Fullscreen, volume, sound effects, and animations are configurable in-app
- Settings persist across restarts via the backend API
