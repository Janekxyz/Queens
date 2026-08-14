package com.jaxjack.queens.core.time

interface TimeProvider {

    fun currentTimeMillis(): Long

    fun elapsedRealtimeMillis(): Long
}
