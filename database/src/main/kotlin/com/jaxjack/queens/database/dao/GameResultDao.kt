package com.jaxjack.queens.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jaxjack.queens.database.entity.GameResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultDao {

    @Query("SELECT * FROM game_result ORDER BY duration ASC")
    fun observeAll(): Flow<List<GameResultEntity>>

    @Insert
    suspend fun insert(gameResultEntity: GameResultEntity)
}