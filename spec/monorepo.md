# Monorepo Migration Specification

**Project:** Friday/Jarvis
**Version:** 1.0.0
**Date:** 2026-02-07
**Status:** Implemented

## Overview

This document specifies the migration of the Friday/Jarvis Android application from a single-module project to a multi-module monorepo architecture. The monorepo encompasses an Android mobile app, Kotlin/Ktor backend API, shared KMP models, CLI client, Terraform infrastructure, and CI/CD automation.

## Architecture

### Module Structure

```
friday/
├── mobile/app/              # Android application (Circuit + Hilt)
├── backend/server/          # Ktor REST API server
├── shared/models/           # Kotlin Multiplatform shared models
├── cli/                     # Clikt terminal client
├── infra/                   # Terraform infrastructure-as-code
├── build-logic/convention/  # Gradle convention plugins
├── .github/workflows/       # GitHub Actions CI/CD
└── docs/                    # Architecture documentation
```

### Gradle Modules

| Module | Path | Description | Dependencies |
|--------|------|-------------|--------------|
| Mobile App | `:mobile:app` | Android app with Circuit UI | `:shared:models` (optional) |
| Backend Server | `:backend:server` | Ktor REST API | `:shared:models` |
| Shared Models | `:shared:models` | KMP data models (JVM target) | None |
| CLI Client | `:cli` | Terminal client | `:shared:models` |

### Dependency Graph

```
:mobile:app ──┐
              ├──> :shared:models
:backend:server ──┤
              │
:cli ─────────┘
```

## Technology Stack

### Build System
- **Gradle:** 8.14
- **Android Gradle Plugin:** 8.12.0
- **Kotlin:** 2.2.0
- **KSP:** 2.2.0-2.0.2

### Mobile (Android)
- **Compose BOM:** 2025.07.00
- **Circuit:** 0.30.0 (UI framework)
- **Hilt:** 2.57 (Dependency injection)
- **Room:** 2.7.1 (Local database)
- **Retrofit:** 2.9.0 (HTTP client)
- **WorkManager:** 2.9.0 (Background tasks)

### Backend (Ktor)
- **Ktor:** 3.1.1
- **Logback:** 1.5.16
- **Firebase Admin SDK:** 9.4.3
- **kotlinx-serialization:** (via Kotlin plugin)

### Shared (KMP)
- **kotlinx-coroutines:** 1.10.1
- **kotlinx-datetime:** 0.6.2
- **kotlinx-serialization:** (via Kotlin plugin)

### CLI
- **Clikt:** 5.0.3
- **Mosaic:** 0.14.0 (Terminal UI, currently unused)
- **Ktor Client:** 3.1.1 (CIO engine)

### Infrastructure
- **Terraform:** ≥ 1.5.0
- **Google Cloud Provider:** ~> 6.0

## Modules Specification

### 1. Mobile App (`:mobile:app`)

**Location:** `mobile/app/`
**Type:** Android Application
**Package:** `dev.arunkumar.jarvis`

#### Structure
```
mobile/app/src/main/java/dev/arunkumar/jarvis/
├── di/                    # Hilt modules
│   ├── AppModule.kt
│   └── CircuitModule.kt
├── data/
│   ├── permissions/       # Permission management system
│   ├── ticktick/          # TickTick integration (REST + Room)
│   ├── repository/        # Data repositories
│   └── service/           # API services
├── service/               # Android services (accessibility, notifications)
└── ui/
    ├── screens/           # Circuit UI screens
    └── theme/             # Material3 theming
```

#### Key Features
- **Permission System:** 11 permission types organized into 6 feature groups
- **TickTick Integration:** REST API proxy with Room offline cache
- **Circuit UI:** Unidirectional data flow with Presenters and State
- **Hilt DI:** Dependency injection throughout

#### Build Configuration
- **Plugins:** `jarvis.local-properties`, `jarvis.managed-devices`
- **Dependencies:** Optionally depends on `:shared:models` (currently unused)
- **Min SDK:** (Inherited from existing configuration)

#### Migration Notes
- **Path Change:** Moved from `app/` to `mobile/app/` via `git mv`
- **Module Path:** Changed from `:app` to `:mobile:app` in `settings.gradle.kts`
- **No Code Changes:** Existing functionality preserved

### 2. Backend Server (`:backend:server`)

**Location:** `backend/server/`
**Type:** Ktor Application
**Package:** `dev.arunkumar.jarvis.server`

#### Structure
```
backend/server/src/main/kotlin/dev/arunkumar/jarvis/server/
├── Application.kt         # Main entry point
├── plugins/
│   ├── Serialization.kt   # JSON content negotiation
│   ├── Monitoring.kt      # Call logging
│   ├── Authentication.kt  # Firebase JWT (placeholder)
│   └── Routing.kt         # Route configuration
├── routes/
│   ├── HealthRoutes.kt    # GET /health
│   ├── UserRoutes.kt      # GET/PUT /api/user/*
│   └── AiRoutes.kt        # POST /api/ai/chat
└── config/
    └── AppConfig.kt       # Environment configuration
```

#### API Endpoints

| Endpoint | Method | Description | Status |
|----------|--------|-------------|--------|
| `/health` | GET | Health check | ✅ Implemented |
| `/api/user/profile` | GET | Get user profile | 🟡 Placeholder |
| `/api/user/profile` | PUT | Update user profile | 🟡 Placeholder |
| `/api/user/settings` | GET | Get user settings | 🟡 Placeholder |
| `/api/user/settings` | PUT | Update user settings | 🟡 Placeholder |
| `/api/ai/chat` | POST | AI chat request | 🟡 Placeholder |

#### Configuration
- **Port:** 8080 (configurable via `PORT` env var)
- **Environment:** Configurable via `ENVIRONMENT` env var
- **GCP Project:** Configurable via `GCP_PROJECT_ID` env var

#### Deployment
- **Dockerfile:** Single-stage build using `eclipse-temurin:21-jre-alpine`
- **Target:** Google Cloud Run
- **Image:** `{region}-docker.pkg.dev/{project}/jarvis/server:latest`

#### Implementation Status
- ✅ **Complete:** Plugin architecture, routing, health check, JSON serialization
- 🟡 **Placeholder:** Firebase Auth, Firestore integration, AI provider integration

### 3. Shared Models (`:shared:models`)

**Location:** `shared/models/`
**Type:** Kotlin Multiplatform Library (JVM target only)
**Package:** `dev.arunkumar.jarvis.shared.models`

#### Structure
```
shared/models/src/commonMain/kotlin/dev/arunkumar/jarvis/shared/models/
├── user/
│   ├── UserProfile.kt     # User identity and profile
│   └── UserSettings.kt    # User preferences
├── task/
│   ├── TaskPriority.kt    # Priority enum (NONE, LOW, MEDIUM, HIGH)
│   ├── TaskItem.kt        # Task data model
│   └── TaskProject.kt     # Project/folder model
└── ai/
    ├── AiProvider.kt      # AI provider enum
    ├── AiRequest.kt       # AI chat request
    └── AiResponse.kt      # AI chat response
```

#### Model Specifications

##### UserProfile
```kotlin
@Serializable
data class UserProfile(
    val id: String,
    val name: String,
    val email: String,
    val avatarUrl: String? = null
)
```

##### UserSettings
```kotlin
@Serializable
data class UserSettings(
    val isDarkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val language: String = "en"
)
```

##### TaskPriority
```kotlin
@Serializable
enum class TaskPriority(val level: Int) {
    NONE(0),
    LOW(1),
    MEDIUM(3),
    HIGH(5)
}
```

##### TaskItem
```kotlin
@Serializable
data class TaskItem(
    val id: String,
    val projectId: String,
    val title: String,
    val content: String? = null,
    val priority: TaskPriority = TaskPriority.NONE,
    val status: String,
    val dueDate: Instant? = null,
    val tags: List<String> = emptyList()
)
```

##### TaskProject
```kotlin
@Serializable
data class TaskProject(
    val id: String,
    val name: String,
    val color: String? = null,
    val sortOrder: Int = 0
)
```

##### AiProvider
```kotlin
@Serializable
enum class AiProvider {
    VERTEX_GEMINI,
    CLAUDE_API,
    OPENAI
}
```

##### AiRequest
```kotlin
@Serializable
data class AiRequest(
    val provider: AiProvider,
    val prompt: String,
    val systemInstruction: String? = null,
    val maxTokens: Int? = null,
    val temperature: Double? = null
)
```

##### AiResponse
```kotlin
@Serializable
data class AiResponse(
    val provider: AiProvider,
    val content: String,
    val model: String,
    val tokensUsed: Int? = null,
    val durationMs: Long? = null
)
```

#### Design Decisions
- **JVM-only:** Uses `jvm()` target instead of full multiplatform
- **Future-proof:** KMP structure allows adding Android/iOS targets later
- **Immutability:** All models are immutable data classes with `val` properties
- **Serialization:** All models annotated with `@Serializable`
- **Datetime:** Uses `kotlinx.datetime.Instant` for portable datetime handling

### 4. CLI Client (`:cli`)

**Location:** `cli/`
**Type:** Kotlin Application
**Package:** `dev.arunkumar.jarvis.cli`

#### Structure
```
cli/src/main/kotlin/dev/arunkumar/jarvis/cli/
├── Main.kt                    # Entry point
├── commands/
│   ├── RootCommand.kt         # Main command with --server-url
│   ├── TaskCommand.kt         # tasks subcommand
│   └── AiCommand.kt           # ai subcommand
└── client/
    └── JarvisClient.kt        # HTTP client wrapper
```

#### Command Structure

```
jarvis [--server-url=<url>]
├── tasks
│   ├── list              # List all tasks (TODO)
│   └── show <task-id>    # Show task details (TODO)
└── ai <prompt>           # AI chat (functional)
```

#### Configuration
- **Default Server URL:** `http://localhost:8080`
- **Configurable:** Via `--server-url` flag
- **Distribution:** Built via `installDist` Gradle task

#### Implementation Status
- ✅ **Complete:** Command hierarchy, server URL configuration, AI chat
- 🟡 **Placeholder:** Task list and show commands

### 5. Terraform Infrastructure (`infra/`)

**Location:** `infra/`
**Type:** Terraform Configuration

#### Root Configuration

| File | Purpose |
|------|---------|
| `versions.tf` | Terraform and provider version constraints |
| `variables.tf` | Input variables (project_id, region, environment) |
| `main.tf` | Module instantiation with dependencies |
| `outputs.tf` | Exported values (URLs, names) |
| `terraform.tfvars.example` | Example variable values |
| `.gitignore` | State and variable file exclusions |

#### Modules

##### 1. Artifact Registry (`modules/artifact-registry/`)
- **Purpose:** Docker image repository for backend
- **Resources:** `google_artifact_registry_repository.jarvis`
- **Configuration:** Docker format, labeled with environment

##### 2. Cloud Run (`modules/cloud-run/`)
- **Purpose:** Backend service hosting
- **Resources:**
  - `google_cloud_run_v2_service.jarvis_server`
  - `google_cloud_run_v2_service_iam_member.public_access`
- **Configuration:**
  - Scaling: 0-3 instances
  - Memory: 512Mi
  - CPU: 1
  - Port: 8080
  - Public access: `allUsers` with `roles/run.invoker`

##### 3. Firestore (`modules/firestore/`)
- **Purpose:** NoSQL database for user data
- **Resources:** `google_firestore_database.jarvis`
- **Configuration:** Native mode, default location
- **Deletion Policy:** `DELETE`

##### 4. Cloud Functions (`modules/cloud-functions/`)
- **Purpose:** Placeholder for future IFTTT webhooks
- **Resources:** `google_storage_bucket.function_source`
- **Status:** Placeholder only, no functions deployed

##### 5. Pub/Sub (`modules/pubsub/`)
- **Purpose:** Event messaging system
- **Resources:**
  - `google_pubsub_topic.events` (jarvis-events)
  - `google_pubsub_subscription.events_subscription`
- **Configuration:** 20s ack deadline

##### 6. Secret Manager (`modules/secrets/`)
- **Purpose:** API key storage
- **Resources:** 4 secrets with auto-replication
  - `claude-api-key`
  - `openai-api-key`
  - `firebase-service-account`
  - `ticktick-api-key`

#### State Management
- **Current:** Local state (default)
- **Recommended:** Migrate to GCS backend for production

#### Variables

| Variable | Type | Default | Description |
|----------|------|---------|-------------|
| `project_id` | string | (required) | GCP project ID |
| `region` | string | `us-central1` | GCP region |
| `environment` | string | `prod` | Environment name |

### 6. Convention Plugins (`build-logic/convention/`)

**Location:** `build-logic/convention/src/main/kotlin/`

#### Plugin Registry

| Plugin ID | Class | Purpose |
|-----------|-------|---------|
| `jarvis.local-properties` | `LocalPropertiesPlugin` | Load local.properties |
| `jarvis.managed-devices` | `GradleManagedDevicesPlugin` | Configure GMD testing |
| `jarvis.kmp-library` | `KmpLibraryPlugin` | KMP JVM-only setup |
| `jarvis.ktor-server` | `KtorServerPlugin` | Ktor server configuration |
| `jarvis.cli-application` | `CliApplicationPlugin` | CLI app configuration |

#### KmpLibraryPlugin

**Applied to:** `:shared:models`

**Behavior:**
- Applies `kotlin-multiplatform` plugin
- Applies `kotlin-serialization` plugin
- Configures `jvm()` target
- Adds dependencies to `commonMain`:
  - `kotlinx-serialization-json`
  - `kotlinx-coroutines-core`
  - `kotlinx-datetime`
- Adds `kotlin-test` to `commonTest`

**Example Usage:**
```kotlin
plugins {
    id("jarvis.kmp-library")
}
```

#### KtorServerPlugin

**Applied to:** `:backend:server`

**Behavior:**
- Applies `io.ktor.plugin`
- Applies `kotlin-jvm`
- Applies `kotlin-serialization`

**Example Usage:**
```kotlin
plugins {
    id("jarvis.ktor-server")
}

application {
    mainClass.set("dev.arunkumar.jarvis.server.ApplicationKt")
}
```

#### CliApplicationPlugin

**Applied to:** `:cli`

**Behavior:**
- Applies `kotlin-jvm`
- Applies `kotlin-serialization`
- Applies `application`

**Example Usage:**
```kotlin
plugins {
    id("jarvis.cli-application")
}

application {
    mainClass.set("dev.arunkumar.jarvis.cli.MainKt")
}
```

### 7. CI/CD Workflows (`.github/workflows/`)

#### Android Workflow (`android.yml`)

**Triggers:**
- Push/PR to `main` or `develop`
- Path filters: `mobile/**`, `shared/**`, `build-logic/**`, `gradle/**`, `*.gradle.kts`, `gradle.properties`

**Jobs:**
1. **build** (ubuntu-latest)
   - Setup JDK 21 (Temurin)
   - Setup Gradle with caching
   - Build: `:mobile:app:assembleDebug`
   - Test: `:mobile:app:test`
   - Lint: `:mobile:app:lint`
   - Test shared: `:shared:models:jvmTest`
   - Upload APK artifact (7 day retention)
   - Upload test results (7 day retention)

#### Backend Workflow (`backend.yml`)

**Triggers:**
- Push/PR to `main` or `develop`
- Path filters: `backend/**`, `shared/**`, `build-logic/**`, `gradle/**`, `*.gradle.kts`

**Jobs:**
1. **test** (ubuntu-latest)
   - Setup JDK 21
   - Run: `:backend:server:test`
   - Upload test results

2. **deploy** (ubuntu-latest, main only)
   - **Depends on:** test
   - **Permissions:** `contents: read`, `id-token: write`
   - Setup JDK 21
   - Build Fat JAR: `:backend:server:buildFatJar`
   - Authenticate to GCP (WIF)
   - Setup gcloud
   - Build Docker image with SHA tag
   - Push to Artifact Registry
   - Deploy to Cloud Run

**Required Secrets:**
- `WIF_PROVIDER` - Workload Identity Federation provider
- `WIF_SERVICE_ACCOUNT` - GCP service account email

**Required Variables:**
- `GCP_PROJECT_ID` - GCP project ID
- `GCP_REGION` - GCP region (default: us-central1)

#### Terraform Workflow (`terraform.yml`)

**Triggers:**
- Push/PR to `main`
- Path filters: `infra/**`

**Jobs:**
1. **plan** (ubuntu-latest)
   - **Permissions:** `contents: read`, `id-token: write`, `pull-requests: write`
   - Setup Terraform (~> 1.5)
   - Authenticate to GCP (WIF)
   - Run: `terraform init`, `validate`, `plan`
   - Upload plan artifact (5 day retention)

2. **apply** (ubuntu-latest, main only)
   - **Depends on:** plan
   - **Environment:** production (requires approval)
   - **Permissions:** `contents: read`, `id-token: write`
   - Setup Terraform
   - Authenticate to GCP
   - Run: `terraform init`, `apply -auto-approve`

## Build Commands

### Mobile
```bash
./gradlew :mobile:app:assembleDebug            # Build debug APK
./gradlew :mobile:app:test                     # Run unit tests
./gradlew :mobile:app:lint                     # Lint check
./gradlew :mobile:app:connectedAndroidTest     # Instrumented tests
./gradlew :mobile:app:installDebug             # Install on device
```

### Backend
```bash
./gradlew :backend:server:test                 # Run tests
./gradlew :backend:server:buildFatJar          # Build deployable JAR
./gradlew :backend:server:run                  # Run locally
```

### Shared
```bash
./gradlew :shared:models:jvmTest               # Run JVM tests
./gradlew :shared:models:assemble              # Build module
```

### CLI
```bash
./gradlew :cli:test                            # Run tests
./gradlew :cli:installDist                     # Build distribution
./cli/build/install/cli/bin/cli --help         # Run CLI
```

### Infrastructure
```bash
cd infra && terraform init                     # Initialize
cd infra && terraform validate                 # Validate config
cd infra && terraform plan                     # Plan changes
cd infra && terraform apply                    # Apply changes
```

## Verification Status

All builds passing (verified 2026-02-07):
- ✅ `:mobile:app:assembleDebug` - 1m 18s
- ✅ `:backend:server:test` - 1s
- ✅ `:shared:models:jvmTest` - 1s
- ✅ `:cli:test` - 445ms
- ✅ `terraform validate` - Valid configuration

## Implementation Status

### ✅ Complete
- Directory restructure (app → mobile/app)
- Shared models module (all 8 models)
- Backend server structure (plugins, routes, config)
- CLI client structure (commands, client wrapper)
- Terraform modules (6 modules)
- CI/CD workflows (3 workflows)
- Convention plugins (3 new plugins)
- Documentation (CLAUDE.md, docs/Arch.md)

### 🟡 Placeholder (TODOs in code)
- Backend Firebase Auth implementation
- Backend Firestore integration
- Backend AI provider integration
- CLI task list/show commands
- Cloud Functions IFTTT webhooks
- Secret Manager secret versions

### ⚠️ Optional
- Mobile app: Remove unused `:shared:models` dependency OR migrate to use it

## Migration Notes

### Git History Preservation
- Used `git mv app mobile/app` to preserve blame and history
- All Android app files maintain git history

### Breaking Changes
- Module path changed: `:app` → `:mobile:app`
- Build commands must use new module path
- Documentation updated to reflect new structure

### Configuration Changes
- `settings.gradle.kts`: Updated include path
- `CLAUDE.md`: Complete rewrite with new structure
- `docs/Arch.md`: Added new architecture sections
- `.gitignore`: Added Terraform and CLI patterns

## Future Considerations

### Multiplatform Expansion
The shared models module uses KMP structure (`commonMain`) but only JVM target. To add Android/iOS:
1. Add `android()` or `iosArm64()` targets to `KmpLibraryPlugin`
2. Update shared/models/build.gradle.kts
3. Test platform-specific serialization

### State Management
Current Terraform uses local state. For production:
1. Create GCS bucket for state storage
2. Add backend configuration to `infra/versions.tf`
3. Run `terraform init -migrate-state`

### Authentication
Backend requires Firebase Auth implementation:
1. Configure Firebase project
2. Implement JWT verification in `Authentication.kt`
3. Wrap protected routes in `authenticate {}` blocks
4. Update mobile app to send ID tokens

### Mobile Integration
Mobile app currently has unused `:shared:models` dependency. Options:
1. **Remove dependency:** Delete from mobile/app/build.gradle.kts
2. **Migrate to shared models:** Replace local UserProfile/UserSettings with shared models

## Appendix

### Version Catalog Additions

See `gradle/libs.versions.toml` for complete list. Key additions:
- Ktor 3.1.1 (server and client modules)
- Firebase Admin SDK 9.4.3
- Clikt 5.0.3
- Mosaic 0.14.0
- kotlinx-coroutines 1.10.1
- kotlinx-datetime 0.6.2

### Convention Plugin Pattern

All convention plugins follow this pattern:
```kotlin
class MyPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            plugins.apply("base.plugin.id")
            // Configuration
        }
    }
}
```

Registered in `build-logic/convention/build.gradle.kts`:
```kotlin
gradlePlugin {
    plugins {
        register("myPlugin") {
            id = "jarvis.my-plugin"
            implementationClass = "MyPlugin"
        }
    }
}
```

---

**Document Version:** 1.0.0
**Last Updated:** 2026-02-07
**Maintained By:** Friday Development Team
