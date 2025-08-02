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