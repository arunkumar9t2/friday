package dev.arunkumar.jarvis.di

import com.slack.circuit.foundation.Circuit
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
    fun provideCircuit(): Circuit {
        return Circuit.Builder()
            .addPresenter<HomeScreen> { _, navigator, _ ->
                HomePresenter(navigator)
            }
            .addPresenter<DetailsScreen> { screen, navigator, _ ->
                DetailsPresenter(screen, navigator)
            }
            .addPresenter<SettingsScreen> { _, navigator, _ ->
                SettingsPresenter(navigator)
            }
            .addUi<HomeScreen> { state, modifier ->
                HomeUi(state, modifier)
            }
            .addUi<DetailsScreen> { state, modifier ->
                DetailsUi(state, modifier)
            }
            .addUi<SettingsScreen> { state, modifier ->
                SettingsUi(state, modifier)
            }
            .build()
    }
}