package com.jaxjack.queens.features.gameresult.api

import kotlinx.coroutines.flow.Flow

interface GameResultRepository {

    suspend fun insert(draft: GameResultDraft)

    fun observeAll(): Flow<List<GameResult>>
}