package com.jaxjack.queens.database.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("game_result")
data class GameResultEntity(
    @PrimaryKey val id: String,
    @ColumnInfo("duration") val duration: Long,
    @ColumnInfo("board_size") val boardSize: Long,
    @ColumnInfo("created_at") val createdAt: Long
)