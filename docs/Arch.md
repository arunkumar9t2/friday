# UI Architecture

This document outlines the UI architecture patterns used in Astra. The app uses [Circuit](https://slackhq.github.io/circuit/) as its UI architecture framework, following a unidirectional data flow pattern.

## Core Components

### 1. Screen

Screens represent distinct UI states in the application. They are Kotlin data classes that:

- Implement the `Screen` interface from Circuit
- Are annotated with `@Parcelize` for state preservation
- Define the contract between UI and presentation layer
- Contain associated State and Event classes

Example:
```kotlin
@Parcelize
data class LauncherScreen(
  private val empty: String = ""
) : Screen {
  @Stable
  @Immutable
  data class State(
    val notificationState: NotificationState,
    val calendarState: CalendarState,
    val t9State: T9State,
    val eventSink: (Event) -> Unit
  ) : CircuitUiState

  sealed interface Event : CircuitUiEvent {
    data object SwipeDown : Event
    data object SwipeUp : Event
  }
}
```

### 2. UI Components

UI components are Composable functions that:

- Are annotated with `@CircuitInject` for dependency injection
- Take a State object and optional Modifier as parameters
- Handle the visual representation of the screen
- Use the eventSink to dispatch user actions

Example:
```kotlin
@CircuitInject(LauncherScreen::class, ActivityComponent::class)
@Composable
fun LauncherUi(
  state: LauncherScreen.State,
  modifier: Modifier = Modifier
) {
  // UI implementation
}
```

### 3. Presenter

Presenters handle the business logic and state management:

- Are created using `@AssistedInject` for dependency injection
- Implement the `Presenter<State>` interface
- Handle state updates and event processing
- Coordinate with repositories and other data sources
- Use coroutines for asynchronous operations

Example:
```kotlin
class T9Presenter @AssistedInject constructor(
  @Assisted private val screen: T9Screen,
  private val t9Manager: T9Manager
) : Presenter<T9Screen.State> {
  @Composable
  override fun present(): T9Screen.State {
    // State management and event handling
  }
}
```

### 4. Events

Events represent user actions or system events:

- Are defined as sealed interfaces/classes within the Screen class
- Implement `CircuitUiEvent`
- Use descriptive names for actions
- Can carry additional data when needed

Example:
```kotlin
sealed interface Event : CircuitUiEvent {
  data object SwipeDown : Event
  data class NumberPressed(val number: String) : Event
  data class ResultSelected(val result: SearchResult) : Event
}
```

## State Management

### 1. UI State

- Immutable data classes implementing `CircuitUiState`
- Contains all data needed to render the UI
- Includes an eventSink for dispatching actions
- Uses `@Stable` and `@Immutable` annotations for composition optimization

### 2. State Updates

- Handled in the Presenter's `present()` method
- Use `remember` and `mutableStateOf` for local state
- Utilize `LaunchedEffect` for side effects
- Handle state updates in a predictable manner

## Code Generation

Circuit uses code generation for dependency injection:

1. Screen Components:
   - Annotate with `@CircuitInject`
   - Specify the Screen class and component (e.g., ActivityComponent)

2. Presenter Factory:
   ```kotlin
   @CircuitInject(T9Screen::class, ActivityComponent::class)
   @AssistedFactory
   fun interface Factory {
     fun create(screen: T9Screen): T9Presenter
   }
   ```

## Best Practices

1. State Organization:
   - Keep state classes focused and minimal
   - Use data classes for immutable state
   - Group related states together

2. Event Handling:
   - Use descriptive event names
   - Keep events simple and focused
   - Handle events in a predictable manner

3. UI Components:
   - Keep composables focused on UI rendering
   - Use proper modifier hierarchy
   - Follow Compose best practices

4. Presenter Logic:
   - Keep business logic in presenters
   - Use coroutines for async operations
   - Handle state updates predictably

## Example Flow

1. User interaction triggers an event
2. Event is dispatched through eventSink
3. Presenter processes the event
4. State is updated based on event processing
5. UI recomposes with new state

## Testing

The architecture supports testing at multiple levels:

1. UI Tests:
   - Test composables in isolation
   - Verify UI state rendering
   - Test user interactions

2. Presenter Tests:
   - Test business logic
   - Verify state updates
   - Test event handling

3. Integration Tests:
   - Test complete screen flows
   - Verify component interaction
   - Test state preservation

## Dependencies

- Circuit: UI architecture framework
- Compose: UI toolkit
- Hilt: Dependency injection
- Coroutines: Asynchronous operations

## Further Reading

- [Circuit Documentation](https://slackhq.github.io/circuit/)
- [Compose Documentation](https://developer.android.com/jetpack/compose)
- [Kotlin Coroutines](https://kotlinlang.org/docs/coroutines-overview.html)

---

# Monorepo Architecture

## Monorepo Structure

The Jarvis project is organized as a monorepo with multiple modules:

- **mobile/app**: Android frontend (Circuit + Hilt)
- **backend/server**: Ktor REST API (Firebase Auth, Firestore)
- **shared/models**: KMP shared models (JVM target)
- **cli**: Clikt+Mosaic terminal client
- **infra**: Terraform IaC for GCP

## Backend Architecture (backend/server)

### Tech Stack
- **Ktor 3.1.1**: HTTP server and routing
- **kotlinx-serialization**: JSON serialization
- **Firebase Admin SDK**: Authentication
- **Logback**: Logging

### Key Components

**Plugins** (`plugins/`):
- `Serialization.kt`: JSON content negotiation with kotlinx-serialization
- `Monitoring.kt`: Request logging with CallLogging
- `Authentication.kt`: Firebase JWT authentication (placeholder)
- `Routing.kt`: Route configuration

**Routes** (`routes/`):
- `HealthRoutes.kt`: Health check endpoint
- `UserRoutes.kt`: User profile and settings management
- `AiRoutes.kt`: AI provider integration

**Configuration**:
- `AppConfig.kt`: Environment-based configuration (port, GCP project)
- `application.yaml`: Ktor server configuration
- `logback.xml`: Logging configuration

### Deployment
- Docker image built from `Dockerfile` (eclipse-temurin:21-jre-alpine)
- Deployed to Cloud Run via GitHub Actions
- Uses Workload Identity Federation for GCP authentication

## Shared Models (shared:models)

Kotlin Multiplatform module targeting JVM. Provides serializable data classes for cross-module communication.

### Domain Models

**User Domain** (`user/`):
- `UserProfile`: User identity and profile information
- `UserSettings`: User preferences

**Task Domain** (`task/`):
- `TaskPriority`: Priority enumeration
- `TaskItem`: Task data with metadata
- `TaskProject`: Project/folder organization

**AI Domain** (`ai/`):
- `AiProvider`: Supported AI providers (Vertex Gemini, Claude API, OpenAI)
- `AiRequest`: AI chat request
- `AiResponse`: AI chat response with metadata

All models use `@Serializable` from kotlinx-serialization.

## CLI Architecture (cli/)

Terminal client for interacting with backend API.

### Tech Stack
- **Clikt 5.0.3**: Command-line interface framework
- **Mosaic 0.14.0**: Terminal UI runtime
- **Ktor Client**: HTTP client for backend communication

### Commands

**RootCommand**:
- Accepts `--server-url` option (default: localhost:8080)
- Passes URL to subcommands via Clikt context

**TaskCommand**:
- `list`: Fetch and display tasks from backend
- `show <id>`: Display task details

**AiCommand**:
- `<prompt>`: Send prompt to backend AI endpoint

**JarvisClient**:
- Ktor HTTP client wrapper
- Uses shared models for request/response serialization

## Convention Plugins (build-logic/convention)

Five custom Gradle convention plugins:

1. **jarvis.local-properties**: Loads `local.properties` (existing)
2. **jarvis.managed-devices**: Configures GMD testing (existing)
3. **jarvis.kmp-library**: Configures KMP module with JVM target, kotlinx-serialization
4. **jarvis.ktor-server**: Configures Ktor server application
5. **jarvis.cli-application**: Configures Kotlin application with serialization

All plugins follow the class-based `Plugin<Project>` pattern.

## CI/CD Architecture

Three GitHub Actions workflows:

**android.yml**:
- Triggers on mobile, shared, build-logic changes
- Runs assembleDebug, test, lint
- Uploads APK and test results

**backend.yml**:
- Test job runs backend tests
- Deploy job (main only): builds Docker, pushes to Artifact Registry, deploys to Cloud Run
- Uses Workload Identity Federation for secure GCP auth

**terraform.yml**:
- Plan job validates and plans infrastructure changes
- Apply job (main only, requires approval): applies to production
- Uses WIF for GCP auth

## Infrastructure (infra/)

Terraform modules for GCP services:

**artifact-registry**: Docker repository for backend images
**cloud-run**: Backend service with auto-scaling (0-3 instances)
**firestore**: Native Firestore database for user data
**cloud-functions**: Placeholder for IFTTT webhook handlers
**pubsub**: Event topic and subscription for async messaging
**secrets**: Secret Manager entries for API keys

All resources tagged with environment labels.