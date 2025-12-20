# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew build                    # Build project
./gradlew test                     # Run all tests
./gradlew test --tests "*.MyTest"  # Run single test class
./gradlew connectedAndroidTest     # Instrumented tests
./gradlew installDebug             # Install debug APK
./gradlew lint                     # Lint check
./gradlew clean                    # Clean build
```

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

## Architecture Overview

Android app using **Circuit** (UI) + **Hilt** (DI) with unidirectional data flow.

**Package**: `dev.arunkumar.jarvis`

### Project Structure

```
app/src/main/java/dev/arunkumar/jarvis/
├── di/                    # Hilt modules (AppModule, CircuitModule, AiModule)
├── data/
│   ├── ai/                # AiProvider interface, AiExecutionState
│   ├── permissions/       # Permission system (11 types, 6 feature groups)
│   ├── termux/            # Termux command execution
│   ├── repository/        # UserRepository
│   └── service/           # ApiService (mock)
└── ui/
    ├── screens/           # Circuit screens (home, details, settings, permissions)
    └── theme/             # Material3 theming
```

### Key Features

**Termux AI Integration** (`data/termux/`):
- `TermuxAiProvider` implements `AiProvider` interface
- Executes scripts via Termux RUN_COMMAND intent
- `TermuxCommandExecutor` manages execution flow with PendingIntent results
- States: Idle → Preparing → Running → Success/Error

**Permission System** (`data/permissions/`):
- `PermissionType.kt` defines DANGEROUS + SPECIAL permissions grouped by feature
- `PermissionManager` - StateFlow-based central state
- `PermissionRequestHandler` - Handles runtime + settings redirects

### Circuit Pattern

Each screen has:
1. **Screen** data class in `Screens.kt` with State/Event interfaces
2. **Presenter** with `@AssistedInject` + `@CircuitInject`
3. **UI** Composable with `@CircuitInject`

Flow: User Action → Event → Presenter → State → UI Recomposition

### Key Files

| Component | Path |
|-----------|------|
| DI Setup | `di/CircuitModule.kt`, `di/AiModule.kt` |
| Screen Definitions | `ui/screens/Screens.kt` |
| Permission Types | `data/permissions/PermissionType.kt` |
| Permission Manager | `data/permissions/PermissionManager.kt` |
| Termux Provider | `data/termux/TermuxAiProvider.kt` |
| Architecture Docs | `docs/Arch.md` |

### Code Style

- All user-facing strings in `res/values/strings.xml`
- Permission rationales in XML string resources, not hardcoded
- Use `@Stable`/`@Immutable` on State classes
