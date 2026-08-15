package com.jaxjack.queens.core.testing

import com.jaxjack.queens.features.gameresult.api.GameResult
import com.jaxjack.queens.features.gameresult.api.GameResultDraft
import com.jaxjack.queens.features.gameresult.api.GameResultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow

class FakeGameResultRepository : GameResultRepository {

    private val results = MutableSharedFlow<List<GameResult>>(replay = 1)

    val inserted = mutableListOf<GameResultDraft>()

    suspend fun emit(results: List<GameResult>) {
        this.results.emit(results)
    }

    override suspend fun insert(gameResultDraft: GameResultDraft) {
        inserted += gameResultDraft
    }

    override fun observeAll(): Flow<List<GameResult>> = results
}
