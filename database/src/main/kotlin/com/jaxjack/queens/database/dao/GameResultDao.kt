package com.jaxjack.queens.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.jaxjack.queens.database.entity.GameResultEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface GameResultDao {

    @Query(
        """
        SELECT * FROM game_result AS result
        WHERE duration = (
            SELECT MIN(duration) FROM game_result WHERE board_size = result.board_size
        )
        GROUP BY board_size
        ORDER BY board_size ASC
        """
    )
    fun observeBestPerBoardSize(): Flow<List<GameResultEntity>>

    @Insert
    suspend fun insert(gameResultEntity: GameResultEntity)
}