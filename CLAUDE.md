# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

### Mobile (Android)
```bash
./gradlew :mobile:app:assembleDebug            # Compile debug APK
./gradlew :mobile:app:test                     # Run unit tests
./gradlew :mobile:app:lint                     # Lint check
./gradlew :mobile:app:test --tests "*.MyTest"  # Run single test
./gradlew :mobile:app:connectedAndroidTest     # Instrumented tests
./gradlew :mobile:app:installDebug             # Install debug APK
```

### Backend (Ktor)
```bash
./gradlew :backend:server:test                 # Run backend tests
./gradlew :backend:server:buildFatJar          # Build deployable JAR
./gradlew :backend:server:run                  # Run server locally
```

### Shared (KMP)
```bash
./gradlew :shared:models:jvmTest               # Run JVM tests
./gradlew :shared:models:assemble              # Build shared module
```

### CLI
```bash
./gradlew :cli:test                            # Run CLI tests
./gradlew :cli:installDist                     # Build distribution
./cli/build/install/cli/bin/cli --help         # Run CLI
```

### Infrastructure
```bash
cd infra && terraform init                     # Initialize Terraform
cd infra && terraform validate                 # Validate configuration
cd infra && terraform plan                     # Plan changes
cd infra && terraform apply                    # Apply changes
```

**Prefer targeted tasks** over monolithic builds.

## Task Tracking with Beads

Work tracked in `.beads/` (git-integrated issue tracker).

**Essential commands**:
```bash
bd ready                              # Pending ready tasks
bd show <id>                          # View task details
bd update <id> --status=in_progress   # Start task
bd close <id> --reason "..."          # Complete task (reason required)
bd blocked                            # See blocked tasks
```

**Dependency inspection**:
```bash
bd dep tree <id> --direction=up    # What this task blocks
bd dep tree <id> --direction=down  # What blocks this task
```

**Workflow**: Pick from ready list → start → finish with reason → refresh

## Project Structure

```
friday/
├── mobile/app/                # Android app (Circuit UI, Hilt DI)
├── backend/server/            # Ktor REST API (Firebase Auth, Firestore)
├── shared/models/             # KMP shared models (JVM target)
├── cli/                       # Clikt+Mosaic terminal client
├── infra/                     # Terraform IaC for GCP
├── build-logic/convention/    # Gradle convention plugins
├── .github/workflows/         # CI/CD pipelines
└── docs/                      # Architecture documentation
```

**Gradle modules**: `:mobile:app`, `:backend:server`, `:shared:models`, `:cli`

## Architecture Overview

Multi-module monorepo with Android frontend, Kotlin backend, shared models, and CLI client.

**Mobile**: `dev.arunkumar.jarvis` — Android app using **Circuit** (UI) + **Hilt** (DI)
**Backend**: `dev.arunkumar.jarvis.server` — Ktor REST API with Firebase Auth
**Shared**: `dev.arunkumar.jarvis.shared.models` — KMP models (user, task, AI)
**CLI**: `dev.arunkumar.jarvis.cli` — Clikt terminal client

**External Dependencies**:
- **TickTick Proxy**: OAuth2 proxy Cloud Run service at `https://ticktick-proxy-j5wtc3hzxq-uc.a.run.app/` (see `spec/ticktick-proxy.md` for architecture)

### Mobile App (mobile/app/)

Android app using Circuit + Hilt with unidirectional data flow.

**Structure**:
```
mobile/app/src/main/java/dev/arunkumar/jarvis/
├── di/                    # Hilt modules (AppModule, CircuitModule)
├── data/
│   ├── permissions/       # Permission system (11 types, 6 feature groups)
│   ├── ticktick/          # TickTick task sync (REST + Room)
│   ├── repository/        # UserRepository
│   └── service/           # ApiService (mock)
└── ui/
    ├── screens/           # Circuit screens (home, details, settings, permissions)
    └── theme/             # Material3 theming
```

### Key Features

**Permission System** (`data/permissions/`):
- `PermissionType.kt` defines DANGEROUS + SPECIAL permissions grouped by feature
- `PermissionManager` - StateFlow-based central state
- `PermissionRequestHandler` - Handles runtime + settings redirects

**TickTick Integration** (`data/ticktick/`):
- REST API → Room cache with on-demand WorkManager sync
- `TickTickRepository` - offline-first task/project access
- `TickTickService` - foreground notification with refresh trigger

### Circuit Pattern

Each screen has:
1. **Screen** data class in `Screens.kt` with State/Event interfaces
2. **Presenter** with `@AssistedInject` + `@CircuitInject`
3. **UI** Composable with `@CircuitInject`

Flow: User Action → Event → Presenter → State → UI Recomposition

## Backend Server (backend/server/)

Ktor REST API running on Cloud Run.

**Key Routes**:
- `GET /health` — Health check
- `GET/PUT /api/user/profile` — User profile management
- `GET/PUT /api/user/settings` — User settings
- `POST /api/ai/chat` — AI provider integration

**Tech Stack**: Ktor 3.1.1, Firebase Admin SDK, kotlinx-serialization

## Shared Models (shared:models)

Kotlin Multiplatform module (JVM target) with serializable data classes:
- **User**: `UserProfile`, `UserSettings`
- **Task**: `TaskItem`, `TaskProject`, `TaskPriority`
- **AI**: `AiProvider`, `AiRequest`, `AiResponse`

Used by backend, CLI, and optionally by mobile app.

## CLI Tool (cli/)

Terminal client using Clikt + Mosaic.

**Commands**:
```bash
jarvis tasks list                  # List tasks
jarvis tasks show <id>             # Task details
jarvis ai <prompt>                 # AI chat
```

**Options**: `--server-url` to specify backend URL (default: localhost:8080)

## Infrastructure (infra/)

Terraform modules for GCP:
- **artifact-registry**: Docker image repository
- **cloud-run**: Backend service hosting
- **firestore**: User data storage
- **cloud-functions**: IFTTT webhook handlers (placeholder)
- **pubsub**: Event messaging
- **secrets**: API key management (Claude, OpenAI, Firebase, TickTick)

## CI/CD (.github/workflows/)

Three GitHub Actions workflows:
- **android.yml**: Build mobile + shared module on push/PR
- **backend.yml**: Test backend, deploy to Cloud Run on main
- **terraform.yml**: Plan on PR, apply on main with approval

**Required Secrets**: `WIF_PROVIDER`, `WIF_SERVICE_ACCOUNT`
**Required Variables**: `GCP_PROJECT_ID`, `GCP_REGION`

### Key Files

| Component | Path |
|-----------|------|
| Mobile DI Setup | `mobile/app/src/main/java/dev/arunkumar/jarvis/di/CircuitModule.kt` |
| Mobile Screen Definitions | `mobile/app/src/main/java/dev/arunkumar/jarvis/ui/screens/Screens.kt` |
| Permission Types | `mobile/app/src/main/java/dev/arunkumar/jarvis/data/permissions/PermissionType.kt` |
| Permission Manager | `mobile/app/src/main/java/dev/arunkumar/jarvis/data/permissions/PermissionManager.kt` |
| TickTick Repository | `mobile/app/src/main/java/dev/arunkumar/jarvis/data/ticktick/TickTickRepository.kt` |
| Backend Application | `backend/server/src/main/kotlin/dev/arunkumar/jarvis/server/Application.kt` |
| Backend Routes | `backend/server/src/main/kotlin/dev/arunkumar/jarvis/server/routes/` |
| Shared Models | `shared/models/src/commonMain/kotlin/dev/arunkumar/jarvis/shared/models/` |
| CLI Commands | `cli/src/main/kotlin/dev/arunkumar/jarvis/cli/commands/` |
| Terraform Modules | `infra/modules/` |
| Architecture Docs | `docs/Arch.md` |

### Code Style

- All user-facing strings in `res/values/strings.xml`
- Permission rationales in XML string resources, not hardcoded
- Use `@Stable`/`@Immutable` on State classes
