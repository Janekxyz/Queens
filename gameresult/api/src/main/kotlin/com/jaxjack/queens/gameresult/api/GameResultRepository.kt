package com.jaxjack.queens.gameresult.api

import kotlinx.coroutines.flow.Flow

interface GameResultRepository {

    suspend fun insert(draft: GameResultDraft)

    fun observeBestPerBoardSize(): Flow<List<GameResult>>
}