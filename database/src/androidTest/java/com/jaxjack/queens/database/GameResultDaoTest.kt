package com.jaxjack.queens.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.jaxjack.queens.database.dao.GameResultDao
import com.jaxjack.queens.database.entity.GameResultEntity
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class GameResultDaoTest {

    private lateinit var database: QueensDatabase
    private lateinit var dao: GameResultDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, QueensDatabase::class.java).build()
        dao = database.gameResultDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `empty database emits no results`() = runTest {
        dao.observeAll().test {
            assertEquals(emptyList<GameResultEntity>(), awaitItem())
        }
    }

    @Test
    fun `inserted result is returned with its columns`() = runTest {
        dao.insert(gameResult(duration = 12_000, boardSize = 4, createdAt = 1_700_000_000_000))

        dao.observeAll().test {
            val stored = awaitItem().single()
            assertEquals(12_000, stored.duration)
            assertEquals(4, stored.boardSize)
            assertEquals(1_700_000_000_000, stored.createdAt)
        }
    }

    @Test
    fun `insert assigns an identifier`() = runTest {
        dao.insert(gameResult(duration = 12_000, boardSize = 4))

        dao.observeAll().test {
            assertTrue(awaitItem().single().id > 0)
        }
    }

    @Test
    fun `insert assigns unique identifiers`() = runTest {
        dao.insert(gameResult(duration = 12_000, boardSize = 4))
        dao.insert(gameResult(duration = 12_000, boardSize = 4))

        dao.observeAll().test {
            val stored = awaitItem()
            assertEquals(2, stored.size)
            assertNotEquals(stored[0].id, stored[1].id)
        }
    }

    @Test
    fun `results are ordered by shortest duration first`() = runTest {
        dao.insert(gameResult(duration = 45_000, boardSize = 6))
        dao.insert(gameResult(duration = 12_000, boardSize = 4))
        dao.insert(gameResult(duration = 30_000, boardSize = 8))

        dao.observeAll().test {
            assertEquals(
                listOf(12_000L, 30_000L, 45_000L),
                awaitItem().map(GameResultEntity::duration)
            )
        }
    }

    @Test
    fun `observeAll emits again when a result is inserted`() = runTest {
        dao.observeAll().test {
            assertEquals(emptyList<GameResultEntity>(), awaitItem())

            dao.insert(gameResult(duration = 12_000, boardSize = 4))

            assertEquals(listOf(12_000L), awaitItem().map(GameResultEntity::duration))
        }
    }

    private fun gameResult(
        duration: Long,
        boardSize: Long,
        createdAt: Long = 0,
    ) = GameResultEntity(
        duration = duration,
        boardSize = boardSize,
        createdAt = createdAt,
    )
}
