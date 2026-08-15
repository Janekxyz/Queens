package com.jaxjack.queens.gameresult.impl

import com.jaxjack.queens.core.time.TimeProvider
import com.jaxjack.queens.database.dao.GameResultDao
import com.jaxjack.queens.gameresult.api.GameResult
import com.jaxjack.queens.gameresult.api.GameResultDraft
import com.jaxjack.queens.gameresult.api.GameResultRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GameResultRepositoryImpl @Inject constructor(
    private val gameResultDao: GameResultDao,
    private val timeProvider: TimeProvider
) : GameResultRepository {

    override suspend fun insert(draft: GameResultDraft) {
        gameResultDao.insert(
            gameResultEntity = draft.toEntity(
                createdAt = timeProvider.currentTimeMillis()
            )
        )
    }

    override fun observeBestPerBoardSize(): Flow<List<GameResult>> {
        return gameResultDao.observeBestPerBoardSize()
            .map { list -> list.map { it.toDomain() } }
    }
}