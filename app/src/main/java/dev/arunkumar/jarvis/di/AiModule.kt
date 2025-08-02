package dev.arunkumar.jarvis.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.arunkumar.jarvis.data.ai.AiProvider
import dev.arunkumar.jarvis.data.termux.TermuxAiProvider

/**
 * Dependency injection module for AI-related components.
 * Binds concrete implementations to abstract interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class AiModule {
    
    /**
     * Bind TermuxAiProvider as the implementation of AiProvider.
     * This can be easily swapped for other implementations later.
     */
    @Binds
    abstract fun bindAiProvider(
        termuxAiProvider: TermuxAiProvider
    ): AiProvider
}