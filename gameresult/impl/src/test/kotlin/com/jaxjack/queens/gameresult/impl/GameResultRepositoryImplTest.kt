package com.jaxjack.queens.gameresult.impl

import app.cash.turbine.test
import com.jaxjack.queens.core.testing.FakeTimeProvider
import com.jaxjack.queens.core.testing.TestCoroutineExtension
import com.jaxjack.queens.database.dao.GameResultDao
import com.jaxjack.queens.database.entity.GameResultEntity
import com.jaxjack.queens.gameresult.api.GameResult
import com.jaxjack.queens.gameresult.api.GameResultDraft
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(TestCoroutineExtension::class)
class GameResultRepositoryImplTest {

    private val dao = FakeGameResultDao()
    private val timeProvider = FakeTimeProvider()
    private val repository = GameResultRepositoryImpl(dao, timeProvider)

    @Test
    fun `insert keeps the duration and board size in their own columns`() = runTest {
        repository.insert(GameResultDraft(duration = 12_000, boardSize = 4))

        val entity = dao.inserted.single()
        assertEquals(12_000, entity.duration)
        assertEquals(4, entity.boardSize)
    }

    @Test
    fun `insert stamps the creation time from the time provider`() = runTest {
        timeProvider.currentTimeMillis = 1_700_000_000_000

        repository.insert(GameResultDraft(duration = 12_000, boardSize = 4))

        assertEquals(1_700_000_000_000, dao.inserted.single().createdAt)
    }

    @Test
    fun `insert leaves the id unset so the database assigns it`() = runTest {
        repository.insert(GameResultDraft(duration = 12_000, boardSize = 4))

        assertEquals(0, dao.inserted.single().id)
    }

    @Test
    fun `observeBestPerBoardSize maps entities to domain models`() = runTest {
        repository.observeBestPerBoardSize().test {
            dao.emit(listOf(GameResultEntity(id = 7, duration = 12_000, boardSize = 4, createdAt = 1_000)))

            assertEquals(
                listOf(GameResult(id = 7, duration = 12_000, boardSize = 4)),
                awaitItem()
            )
        }
    }

    @Test
    fun `observeBestPerBoardSize keeps the order the dao emits`() = runTest {
        repository.observeBestPerBoardSize().test {
            dao.emit(
                listOf(
                    GameResultEntity(id = 1, duration = 45_000, boardSize = 6, createdAt = 1_000),
                    GameResultEntity(id = 2, duration = 12_000, boardSize = 4, createdAt = 2_000),
                )
            )

            assertEquals(listOf(1L, 2L), awaitItem().map(GameResult::id))
        }
    }
}

private class FakeGameResultDao : GameResultDao {

    private val results = MutableSharedFlow<List<GameResultEntity>>(replay = 1)

    val inserted = mutableListOf<GameResultEntity>()

    suspend fun emit(entities: List<GameResultEntity>) {
        results.emit(entities)
    }

    override suspend fun insert(gameResultEntity: GameResultEntity) {
        inserted += gameResultEntity
    }

    override fun observeBestPerBoardSize(): Flow<List<GameResultEntity>> = results
}
