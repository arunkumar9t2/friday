package dev.arunkumar.jarvis.ui.state

/**
 * Marker interface for UI state types.
 * Used for common extension functions across state types.
 */
interface UiState

/**
 * State representation for collections/lists.
 * Use this when displaying lists of items with loading and empty states.
 */
sealed interface ListState<out T> : UiState {
  data object Loading : ListState<Nothing>
  data object Empty : ListState<Nothing>
  data class Loaded<T>(val items: List<T>) : ListState<T>
}

/**
 * State representation for single values.
 * Use this when fetching/displaying a single piece of data.
 */
sealed interface DataState<out T> : UiState {
  data object Loading : DataState<Nothing>
  data class Success<T>(val data: T) : DataState<T>
  data class Error(
    val error: Throwable,
    val message: String? = null
  ) : DataState<Nothing>
}

// ListState extensions
fun <T> ListState<T>.itemsOrNull(): List<T>? = (this as? ListState.Loaded)?.items
fun <T> ListState<T>.itemsOrEmpty(): List<T> = itemsOrNull() ?: emptyList()
fun <T, R> ListState<T>.map(transform: (List<T>) -> List<R>): ListState<R> = when (this) {
  is ListState.Loading -> ListState.Loading
  is ListState.Empty -> ListState.Empty
  is ListState.Loaded -> ListState.Loaded(transform(items))
}

// DataState extensions
fun <T> DataState<T>.dataOrNull(): T? = (this as? DataState.Success)?.data
fun <T, R> DataState<T>.map(transform: (T) -> R): DataState<R> = when (this) {
  is DataState.Loading -> DataState.Loading
  is DataState.Success -> DataState.Success(transform(data))
  is DataState.Error -> this
}

// Common UiState extensions
val UiState.isLoading: Boolean
  get() = this is ListState.Loading || this is DataState.Loading

val UiState.isSuccess: Boolean
  get() = this is ListState.Loaded<*> || this is DataState.Success<*>

val UiState.isError: Boolean
  get() = this is DataState.Error

val UiState.isEmpty: Boolean
  get() = this is ListState.Empty
