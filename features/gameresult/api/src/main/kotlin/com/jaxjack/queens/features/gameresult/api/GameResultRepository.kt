package com.jaxjack.queens.features.gameresult.api

import kotlinx.coroutines.flow.Flow

interface GameResultRepository {

    suspend fun insert(draft: GameResultDraft)

    fun observeBestPerBoardSize(): Flow<List<GameResult>>
}