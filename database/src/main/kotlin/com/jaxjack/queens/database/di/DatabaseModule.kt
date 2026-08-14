package com.jaxjack.queens.database.di

import android.content.Context
import androidx.room.Room
import com.jaxjack.queens.database.QueensDatabase
import com.jaxjack.queens.database.dao.GameResultDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): QueensDatabase =
        Room.databaseBuilder(context, QueensDatabase::class.java, "game_result.db")
            .build()

    @Provides
    fun provideGameResultDao(database: QueensDatabase): GameResultDao =
        database.gameResultDao()

}