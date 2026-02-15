# TickTick Proxy Integration Specification

**Project:** Friday/Jarvis
**Version:** 2.0.0
**Date:** 2026-02-15
**Status:** Deployed & Configured

## Overview

The TickTick Proxy is a standalone Cloud Run service that acts as an OAuth2 intermediary between the Friday mobile app and the TickTick API. It handles OAuth2 authentication server-side (using CLIENT_ID/CLIENT_SECRET) and exposes a simplified Bearer token API to the mobile app, eliminating the need for OAuth2 flows in the Android client.

**Purpose:**
- Centralize OAuth2 credentials in a secure server environment
- Simplify mobile app authentication (Bearer token instead of OAuth2 flow)
- Provide a stable API contract independent of TickTick API changes

**Key Characteristics:**
- **External Dependency:** NOT managed by Terraform, deployed via `gcloud run deploy --source`
- **Stateless:** No database, proxies API calls directly to TickTick
- **Single Responsibility:** Authentication proxy only, no business logic

## Service Details

| Property | Value |
|----------|-------|
| **Service URL** | `https://ticktick-proxy-j5wtc3hzxq-uc.a.run.app/` |
| **Deployment Method** | `gcloud run deploy --source .` (manual, outside Terraform) |
| **GCP Project** | (Same as main Friday infrastructure) |
| **Region** | `us-central1` |
| **Platform** | Cloud Run (fully managed) |
| **Repository** | Separate from this monorepo (not in `friday/` codebase) |

## Authentication Architecture

### Mobile App → Proxy

**Method:** Bearer Token
**Header:** `Authorization: Bearer <TICKTICK_API_KEY>`

The mobile app sends a static API key (`TICKTICK_API_KEY`) configured at build time. This key is:
- Stored in `local.properties` (not committed to git)
- Injected via `LocalPropertiesPlugin` → `project.extra.properties`
- Embedded in `BuildConfig.TICKTICK_API_KEY` at build time
- Attached to all Retrofit requests via OkHttp interceptor

**Security Model:**
- API key is per-developer/per-build (not per-user)
- Intended for personal/development use, not multi-tenant production
- Key rotation requires updating `local.properties` and rebuilding app

### Proxy → TickTick API

**Method:** OAuth2 Authorization Code Flow
**Credentials:** CLIENT_ID + CLIENT_SECRET (managed by proxy service)

The proxy maintains OAuth2 tokens internally and refreshes them as needed. Mobile app is unaware of OAuth2 mechanics.

### SETUP_TOKEN (Admin Only)

**Purpose:** One-time authorization flow to obtain initial OAuth2 tokens
**Usage:** NOT used by mobile app, only by proxy administrator during initial setup
**Flow:** Manual browser-based OAuth2 authorization to generate refresh token

## API Contract

The proxy exposes the following endpoints. **Important:** There is no `GET /task` endpoint to list all tasks — use `GET /tasks` (composite) instead.

### GET /tasks (Composite — Primary Endpoint)

Returns tasks AND projects in a single response. This is the main endpoint used by the mobile app.

**Request:**
```http
GET /tasks
Authorization: Bearer <TICKTICK_API_KEY>
```

**Query Parameters:**
- `completed=true` — Include completed tasks (optional)

**Response:**
```json
{
  "projects": [
    {
      "id": "project-uuid",
      "name": "Project name",
      "color": "#FF5733",
      "sortOrder": 0
    }
  ],
  "tasks": [
    {
      "id": "task-uuid",
      "projectId": "project-uuid",
      "title": "Task title",
      "content": "Task description",
      "priority": 1,
      "status": 0,
      "dueDate": "2026-02-07T10:00:00.000+0000",
      "tags": [
        {
          "name": "tag-name",
          "label": "Tag Label",
          "color": "#FF0000",
          "sortOrder": 0,
          "parent": null,
          "rawName": "tag-name"
        }
      ]
    }
  ],
  "warnings": []
}
```

**Mapping:** `TasksResponse` (see `TasksResponse.kt`)

### GET /project (Pass-through — Not Used by Mobile App)

Direct pass-through to TickTick API. Returns projects only. **Redundant** when using `GET /tasks` which includes projects. Removed from `TickTickApi.kt` in v2.0.0.

**Request:**
```http
GET /project
Authorization: Bearer <TICKTICK_API_KEY>
```

**Response:**
```json
[
  {
    "id": "project-uuid",
    "name": "Project name",
    "color": "#FF5733",
    "sortOrder": 0
  }
]
```

### GET /sync (Delta Sync — Future Use)

Checkpoint-based delta sync. Returns only changes since the last sync.

**Request:**
```http
GET /sync?checkpoint=<value>
Authorization: Bearer <TICKTICK_API_KEY>
```

**Response:** Incremental changes (tasks added/modified/deleted since checkpoint).

**Status:** Not yet used by mobile app. See follow-up task for adoption.

### Error Responses

The proxy returns structured errors:

| Status | Meaning |
|--------|---------|
| 401 | Invalid or missing API key |
| 429 | Rate limit exceeded |
| 502 | TickTick API upstream error |

### Tag Model

Tags are **objects**, not strings. Each tag has:

| Field | Type | Description |
|-------|------|-------------|
| `name` | `String` | Tag identifier |
| `label` | `String?` | Display label |
| `sortOrder` | `Int?` | Sort position |
| `sortType` | `String?` | Sort type |
| `color` | `String?` | Hex color code |
| `parent` | `String?` | Parent tag name |
| `rawName` | `String?` | Raw tag name |

**Mapping:** `ApiTickTickTag` (see `ApiTickTickTag.kt`)

## Mobile Integration Architecture

### Data Flow

```
[User Action]
    ↓
[TickTickRepository.refresh()]
    ↓
[TickTickSyncManager.sync()]
    ↓
[TickTickApi (Retrofit)]  ──[Authorization: Bearer <key>]──>  [ticktick-proxy]
    ↓                                                               ↓
[TasksResponse]          <──[JSON { projects, tasks, warnings }]──  [TickTick API]
    ↓
[response.tasks.map { toEntity(syncTime) }]
[response.projects.map { toEntity(syncTime) }]
    ↓
[TickTickDatabase.replaceAllData()]
    ↓
[Room DAOs insert into SQLite]
    ↓
[Repository queries from TaskDao/ProjectDao]
    ↓
[UI via Circuit Presenters]
```

### Components

#### 1. Network Layer (`TickTickApi.kt`)
```kotlin
interface TickTickApi {
  @GET("tasks")
  suspend fun getTasks(): TasksResponse
}
```

Single composite endpoint replaces the previous two-call approach (`/task` + `/project`).

**Base URL:** `BuildConfig.TICKTICK_API_BASE_URL` = `https://ticktick-proxy-j5wtc3hzxq-uc.a.run.app/`

#### 2. Authentication (`TickTickModule.kt:44-50`)
```kotlin
OkHttpClient.Builder()
  .addInterceptor { chain ->
    chain.proceed(
      chain.request().newBuilder()
        .addHeader("Authorization", "Bearer ${BuildConfig.TICKTICK_API_KEY}")
        .build()
    )
  }
```

Automatically injects Bearer token into all requests.

#### 3. Sync Orchestration (`TickTickSyncManager.kt`)
```kotlin
suspend fun sync(): SyncResult {
  val syncTime = System.currentTimeMillis()
  return try {
    val response = api.getTasks()
    database.replaceAllData(
      response.tasks.map { it.toEntity(syncTime) },
      response.projects.map { it.toEntity(syncTime) }
    )
    SyncResult.Success
  } catch (e: Exception) {
    SyncResult.Error(e)
  }
}
```

**Sync Strategy:** Full replace (not incremental)
**Trigger:** Manual refresh or WorkManager periodic sync

#### 4. Offline Cache (`TickTickDatabase.kt`)
- **Technology:** Room with SQLite backend
- **Tables:** `TaskEntity`, `ProjectEntity`
- **Migration:** Destructive (`fallbackToDestructiveMigration(dropAllTables = true)`)
- **DAOs:** `TaskDao`, `ProjectDao`

#### 5. Repository Layer (`TickTickRepository.kt`)
```kotlin
@Singleton
class TickTickRepository @Inject constructor(
  private val taskDao: TaskDao,
  private val projectDao: ProjectDao,
  private val syncManager: TickTickSyncManager
)
```

**Public API:**
- `suspend fun refresh(): SyncResult` — Triggers network sync
- `suspend fun getPendingTasks(): List<TickTickTask>` — Query local cache
- `suspend fun getAllProjects(): List<TickTickProject>` — Query local cache
- `suspend fun getNotificationItems(limit: Int): List<TickTickNotificationItem>` — Enriched data for UI

#### 6. Background Sync (`TickTickSyncWorker.kt`)
Uses WorkManager to trigger periodic syncs in the background.

## Build Configuration

### 1. API Key Injection Flow

```
local.properties
    TICKTICK_API_KEY=72db1f11-75bc-4fec-b8ef-f62cba9b0b8b
        ↓
LocalPropertiesPlugin (build-logic/convention/)
    Reads local.properties → project.extra.properties
        ↓
mobile/app/build.gradle.kts:27-28
    val ticktickApiKey = project.extra.properties["TICKTICK_API_KEY"]
    buildConfigField("String", "TICKTICK_API_KEY", "\"$ticktickApiKey\"")
        ↓
BuildConfig.java (generated)
    public static final String TICKTICK_API_KEY = "72db1f11-75bc-4fec-b8ef-f62cba9b0b8b";
        ↓
TickTickModule.kt:47
    .addHeader("Authorization", "Bearer ${BuildConfig.TICKTICK_API_KEY}")
```

### 2. Base URL Configuration

**File:** `mobile/app/build.gradle.kts:29`
```kotlin
buildConfigField("String", "TICKTICK_API_BASE_URL",
  "\"https://ticktick-proxy-j5wtc3hzxq-uc.a.run.app/\"")
```

Hardcoded at build time (no runtime configuration).

### 3. Convention Plugin

**Plugin ID:** `jarvis.local-properties`
**Location:** `build-logic/convention/src/main/kotlin/LocalPropertiesPlugin.kt`

**Behavior:**
1. Checks for `local.properties` in project root
2. Loads all properties into `project.extra.properties` map
3. Applied BEFORE Android plugin to ensure properties available during configuration

## Key Files Index

### Mobile App (TickTick Integration)

| File | Purpose |
|------|---------|
| `mobile/app/src/main/java/dev/arunkumar/jarvis/data/ticktick/` | Root package |
| `TickTickModule.kt` | Hilt DI setup (Retrofit, Room, WorkManager) |
| `TickTickApi.kt` | Retrofit interface (single `/tasks` endpoint) |
| `TasksResponse.kt` | Composite response wrapper (`projects` + `tasks` + `warnings`) |
| `ApiTickTickTask.kt` | Network model for tasks |
| `ApiTickTickProject.kt` | Network model for projects |
| `ApiTickTickTag.kt` | Network model for tags (name, color, parent, etc.) |
| `TickTickSyncManager.kt` | Sync orchestration logic |
| `TickTickRepository.kt` | Data repository with offline-first access |
| `TickTickDatabase.kt` | Room database definition |
| `TaskEntity.kt` | Room entity for tasks |
| `ProjectEntity.kt` | Room entity for projects |
| `TaskDao.kt` | Room DAO for tasks |
| `ProjectDao.kt` | Room DAO for projects |
| `TickTickTask.kt` | Domain model for tasks |
| `TickTickProject.kt` | Domain model for projects |
| `TickTickMappers.kt` | Entity ↔ Domain converters |
| `TickTickSyncWorker.kt` | WorkManager background sync |
| `TickTickNotificationItem.kt` | UI-specific data models |
| `TickTickIntents.kt` | Android intent helpers |
| `TickTickRemoteViews.kt` | Notification UI rendering |
| `TickTickPriority.kt` | Priority enum |
| `TickTickDateUtils.kt` | Date comparison utilities |
| `TickTickQualifiers.kt` | Hilt qualifiers (`@TickTickClient`) |

### Build Configuration

| File | Purpose |
|------|---------|
| `local.properties` | API key storage (NOT committed) |
| `build-logic/convention/src/main/kotlin/LocalPropertiesPlugin.kt` | Property injection plugin |
| `mobile/app/build.gradle.kts` | BuildConfig field generation |

### Services (Android)

| File | Purpose |
|------|---------|
| `mobile/app/src/main/java/dev/arunkumar/jarvis/service/TickTickService.kt` | Foreground notification service (if exists) |

## Relationship to Other Tasks

### Separation of Concerns

| Component | Purpose | Status |
|-----------|---------|--------|
| **ticktick-proxy** (this spec) | OAuth2 proxy for mobile app → TickTick API | Deployed & configured |
| **`fr-1ox`** (backend task) | Ktor backend `/api/ticktick/*` endpoints for multi-user sync | Planned, not implemented |

**Key Distinction:**
- **Proxy:** Personal/single-user authentication layer, external to monorepo
- **Backend:** Multi-tenant service with Firestore persistence, part of `:backend:server` module

The mobile app currently uses the proxy directly. In the future, it may migrate to use backend endpoints (which would then call the proxy internally or use separate OAuth2 credentials).

## Verification

### 1. Build Verification
```bash
./gradlew :mobile:app:assembleDebug
```

**Expected:** Build succeeds, `BuildConfig.TICKTICK_API_KEY` populated with actual key.

### 2. Runtime Verification
```kotlin
// Check BuildConfig values (via debugger or test)
BuildConfig.TICKTICK_API_KEY      // "72db1f11-75bc-4fec-b8ef-f62cba9b0b8b"
BuildConfig.TICKTICK_API_BASE_URL // "https://ticktick-proxy-j5wtc3hzxq-uc.a.run.app/"
```

### 3. Network Verification
```bash
# Manual API test (requires valid API key)
curl -H "Authorization: Bearer <TICKTICK_API_KEY>" \
  https://ticktick-proxy-j5wtc3hzxq-uc.a.run.app/tasks
```

**Expected:** JSON object with `projects`, `tasks`, and `warnings` fields.

## Security Considerations

### Strengths
- OAuth2 credentials never exposed to mobile app
- API key only embedded at build time (not hardcoded in source)
- `local.properties` excluded from git via `.gitignore`

### Limitations
- API key not encrypted in APK (recoverable via reverse engineering)
- No per-user authentication (all users with APK share same key)
- No rate limiting or abuse prevention on proxy

### Production Recommendations
1. Migrate to backend service with Firebase Auth for per-user authentication
2. Implement rate limiting on proxy
3. Use ProGuard/R8 obfuscation for BuildConfig fields
4. Rotate API key periodically

## Future Enhancements

### Short-term
- [ ] Adopt `GET /sync` with checkpoint for delta sync (instead of full replace)
- [ ] Add retry logic with exponential backoff in `TickTickSyncManager`
- [ ] Handle 429 rate limit responses with backoff

### Long-term
- [ ] Migrate to `:backend:server` endpoints (see `fr-1ox`)
- [ ] Implement per-user OAuth2 via Firebase Auth
- [ ] Add webhook support for real-time updates
- [ ] Support offline task creation (local-first architecture)

---

**Document Version:** 2.0.0
**Last Updated:** 2026-02-15
**Maintained By:** Friday Development Team
