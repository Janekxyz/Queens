package com.jaxjack.queens.features.gameresult.impl.di

import com.jaxjack.queens.features.gameresult.api.GameResultRepository
import com.jaxjack.queens.features.gameresult.impl.GameResultRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
internal abstract class GameResultModule {

    @Binds
    @Singleton
    abstract fun bindGameResultRepository(impl: GameResultRepositoryImpl): GameResultRepository
}