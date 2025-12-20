# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this
repository.

## Build Commands

- **Build project**: `./gradlew build`
- **Run tests**: `./gradlew test`
- **Run instrumented tests**: `./gradlew connectedAndroidTest`
- **Clean build**: `./gradlew clean`
- **Install debug APK**: `./gradlew installDebug`
- **Lint check**: `./gradlew lint`

## Task Tracking with Beads

Work tracked in `.beads/` (git-integrated issue tracker).

**Essential commands**:
```bash
bd ready                                     # Pending ready tasks
bd show <id>                                 # View task details
bd update <id> --status=in_progress          # Start task
bd close <id> --reason="..."                 # Complete task (reason required)
bd blocked                                   # See blocked tasks
```

**Dependency inspection**:
```bash
bd dep tree <id> --direction=up    # What this task blocks (dependents)
bd dep tree <id> --direction=down  # What blocks this task (dependencies)
bd list --blocked-by <id>          # Tasks blocked by specific issue
```

**Workflow**:
- Pick from scoped ready list -> start -> finish -> refresh
- Prioritize tasks that unblock others (check `Blocks` count in `bd show`)
- Always close tasks with `bd close --reason` when done

**Task hygiene**:
- Always set parent epic: `bd create "Task" --type task --parent <epic-id>`
- Add 1-2 domain labels per task
- Normalize priority for active work (P1-P2)

## Architecture Overview

This is an Android application built with **Circuit** architecture pattern and **Hilt** dependency
injection. The project follows a unidirectional data flow pattern with clear separation of concerns.

### Core Architecture Components

1. **Circuit Framework**: UI architecture using Slack's Circuit library

- Screens are Kotlin data classes implementing the `Screen` interface
- UI components are Composables annotated with `@CircuitInject`
- Presenters handle business logic and state management
- Events represent user actions dispatched through eventSink

2. **Dependency Injection**: Hilt with Circuit integration

- Code generation mode set to "hilt" in `app/build.gradle.kts:46`
- Circuit module configures presenter and UI factories in `di/CircuitModule.kt`
- Components use `@CircuitInject` with `ActivityComponent::class`

3. **Project Structure**:

- `app/src/main/java/dev/arunkumar/jarvis/`
  - `di/` - Dependency injection modules (AppModule, CircuitModule)
  - `ui/screens/` - Screen definitions, presenters, and UI components
  - `ui/theme/` - Compose theme and styling
  - `data/` - Repository and service layer

### Key Dependencies

- Circuit 0.23.1 for UI architecture
- Hilt 2.57 for dependency injection
- Jetpack Compose with Material3
- Kotlin Parcelize for state preservation

### Development Notes

- Uses KSP for code generation with Circuit codegen
- Minimum SDK 28, Target SDK 36
- Kotlin 2.0.21 with Java 11 compatibility
- Detailed architecture documentation available in `docs/Arch.md`

### Code Style Guidelines

- **String Resources**: All user-facing strings should be stored in `app/src/main/res/values/strings.xml`
- **Permission Rationale**: Permission rationale explanations must be in XML string resources, not hardcoded in Kotlin classes
- **Localization**: Use string resources to support future internationalization

### Circuit Pattern Implementation

Each screen follows this pattern:

1. Screen data class with State and Event sealed interfaces
2. UI Composable annotated with `@CircuitInject`
3. Presenter class with `@AssistedInject` handling state and events
4. Factory interface for presenter creation

State flows: User interaction → Event → Presenter → State update → UI recomposition
