package com.jaxjack.queens.features.gameresult.impl

import com.jaxjack.queens.database.entity.GameResultEntity
import com.jaxjack.queens.features.gameresult.api.GameResult
import com.jaxjack.queens.features.gameresult.api.GameResultDraft

internal fun GameResultDraft.toEntity(createdAt: Long) = GameResultEntity(
    duration = duration,
    boardSize = boardSize.toLong(),
    createdAt = createdAt,
)

internal fun GameResultEntity.toDomain() = GameResult(
    id = id,
    duration = duration,
    boardSize = boardSize.toInt(),
)