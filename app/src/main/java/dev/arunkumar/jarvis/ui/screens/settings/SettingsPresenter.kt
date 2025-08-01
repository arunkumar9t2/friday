package dev.arunkumar.jarvis.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.runtime.CircuitUiEvent
import com.slack.circuit.runtime.CircuitUiState
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject

class SettingsPresenter @AssistedInject constructor(
    @Assisted private val navigator: Navigator
) : Presenter<SettingsUiState> {

    @AssistedFactory
    interface Factory {
        fun create(navigator: Navigator): SettingsPresenter
    }

    @Composable
    override fun present(): SettingsUiState {
        var isDarkTheme by remember { mutableStateOf(false) }
        var isNotificationsEnabled by remember { mutableStateOf(true) }
        var selectedLanguage by remember { mutableStateOf("English") }

        return SettingsUiState(
            isDarkTheme = isDarkTheme,
            isNotificationsEnabled = isNotificationsEnabled,
            selectedLanguage = selectedLanguage,
            eventSink = { event ->
                when (event) {
                    SettingsUiEvent.OnBackClicked -> {
                        navigator.pop()
                    }
                    SettingsUiEvent.OnThemeToggled -> {
                        isDarkTheme = !isDarkTheme
                    }
                    SettingsUiEvent.OnNotificationsToggled -> {
                        isNotificationsEnabled = !isNotificationsEnabled
                    }
                    is SettingsUiEvent.OnLanguageChanged -> {
                        selectedLanguage = event.language
                    }
                }
            }
        )
    }
}

@Immutable
data class SettingsUiState(
    val isDarkTheme: Boolean,
    val isNotificationsEnabled: Boolean,
    val selectedLanguage: String,
    val eventSink: (SettingsUiEvent) -> Unit
) : CircuitUiState

sealed interface SettingsUiEvent : CircuitUiEvent {
    object OnBackClicked : SettingsUiEvent
    object OnThemeToggled : SettingsUiEvent
    object OnNotificationsToggled : SettingsUiEvent
    data class OnLanguageChanged(val language: String) : SettingsUiEvent
}