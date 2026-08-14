package com.jaxjack.queens.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.jaxjack.queens.database.dao.GameResultDao
import com.jaxjack.queens.database.entity.GameResultEntity

@Database(entities = [GameResultEntity::class], version = 1)
abstract class QueensDatabase: RoomDatabase() {

    abstract fun gameResultDao(): GameResultDao
}