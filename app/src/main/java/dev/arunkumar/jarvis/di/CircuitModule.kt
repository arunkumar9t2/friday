package dev.arunkumar.jarvis.di

import com.slack.circuit.foundation.Circuit
import com.slack.circuit.runtime.Navigator
import com.slack.circuit.runtime.presenter.Presenter
import com.slack.circuit.runtime.screen.Screen
import com.slack.circuit.runtime.ui.Ui
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.arunkumar.jarvis.ui.screens.DetailsScreen
import dev.arunkumar.jarvis.ui.screens.HomeScreen
import dev.arunkumar.jarvis.ui.screens.SettingsScreen
import dev.arunkumar.jarvis.ui.screens.details.DetailsPresenter
import dev.arunkumar.jarvis.ui.screens.details.DetailsUi
import dev.arunkumar.jarvis.ui.screens.home.HomePresenter
import dev.arunkumar.jarvis.ui.screens.home.HomeUi
import dev.arunkumar.jarvis.ui.screens.settings.SettingsPresenter
import dev.arunkumar.jarvis.ui.screens.settings.SettingsUi
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CircuitModule {

    @Provides
    @Singleton
    fun provideCircuit(
        homePresenterFactory: HomePresenter.Factory,
        detailsPresenterFactory: DetailsPresenter.Factory,
        settingsPresenterFactory: SettingsPresenter.Factory,
        homeUiFactory: HomeUi.Factory,
        detailsUiFactory: DetailsUi.Factory,
        settingsUiFactory: SettingsUi.Factory
    ): Circuit {
        return Circuit.Builder()
            .addPresenter<HomeScreen> { screen, navigator, context ->
                homePresenterFactory.create(navigator)
            }
            .addPresenter<DetailsScreen> { screen, navigator, context ->
                detailsPresenterFactory.create(screen, navigator)
            }
            .addPresenter<SettingsScreen> { screen, navigator, context ->
                settingsPresenterFactory.create(navigator)
            }
            .addUi<HomeScreen> { screen, context ->
                homeUiFactory.create()
            }
            .addUi<DetailsScreen> { screen, context ->
                detailsUiFactory.create()
            }
            .addUi<SettingsScreen> { screen, context ->
                settingsUiFactory.create()
            }
            .build()
    }
}