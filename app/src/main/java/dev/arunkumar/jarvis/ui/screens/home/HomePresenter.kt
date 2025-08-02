package dev.arunkumar.jarvis.ui.screens.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.CircuitContext
import com.slack.circuit.codegen.annotations.CircuitInject
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.components.ActivityComponent
import dev.arunkumar.jarvis.ui.screens.DetailsScreen
import dev.arunkumar.jarvis.ui.screens.HomeScreen
import dev.arunkumar.jarvis.ui.screens.SettingsScreen

class HomePresenter @AssistedInject constructor(
    @Assisted private val screen: HomeScreen,
    @Assisted private val navigator: Navigator,
    @Assisted private val context: CircuitContext
) : Presenter<HomeScreen.State> {

    @Composable
    override fun present(): HomeScreen.State {
        var counter by remember { mutableStateOf(0) }

        return HomeScreen.State(
            title = "Home",
            counter = counter,
            eventSink = { event ->
                when (event) {
                    HomeScreen.Event.OnIncrementClicked -> {
                        counter++
                    }
                    HomeScreen.Event.OnNavigateToDetails -> {
                        navigator.goTo(DetailsScreen(
                            itemId = "item_$counter",
                            title = "Details for Item $counter"
                        ))
                    }
                    HomeScreen.Event.OnNavigateToSettings -> {
                        navigator.goTo(SettingsScreen)
                    }
                }
            }
        )
    }

    @CircuitInject(HomeScreen::class, ActivityComponent::class)
    @AssistedFactory
    interface Factory {
        fun create(
            screen: HomeScreen,
            navigator: Navigator,
            context: CircuitContext
        ): HomePresenter
    }
}

