package dev.arunkumar.jarvis.ui.screens.settings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.codegen.annotations.CircuitInject
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityComponent
import dev.arunkumar.jarvis.ui.screens.SettingsScreen
import dev.arunkumar.jarvis.ui.screens.permissions.PermissionsOverviewScreen

class SettingsPresenter @AssistedInject constructor(
  @Assisted private val screen: SettingsScreen,
  @Assisted private val navigator: Navigator,
) : Presenter<SettingsScreen.State> {

  @Composable
  override fun present(): SettingsScreen.State {
    var isDarkTheme by remember { mutableStateOf(false) }
    var isNotificationsEnabled by remember { mutableStateOf(true) }
    var selectedLanguage by remember { mutableStateOf("English") }

    return SettingsScreen.State(
      isDarkTheme = isDarkTheme,
      isNotificationsEnabled = isNotificationsEnabled,
      selectedLanguage = selectedLanguage,
      eventSink = { event ->
        when (event) {
          SettingsScreen.Event.OnBackClicked -> {
            navigator.pop()
          }

          SettingsScreen.Event.OnThemeToggled -> {
            isDarkTheme = !isDarkTheme
          }

          SettingsScreen.Event.OnNotificationsToggled -> {
            isNotificationsEnabled = !isNotificationsEnabled
          }

          is SettingsScreen.Event.OnLanguageChanged -> {
            selectedLanguage = event.language
          }

          SettingsScreen.Event.OnPermissionsClicked -> {
            navigator.goTo(PermissionsOverviewScreen())
          }
        }
      }
    )
  }

  @CircuitInject(SettingsScreen::class, ActivityComponent::class)
  @AssistedFactory
  interface Factory {
    fun create(
      screen: SettingsScreen,
      navigator: Navigator,
    ): SettingsPresenter
  }
}

