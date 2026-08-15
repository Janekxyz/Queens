package com.jaxjack.queens.core.testing

import com.jaxjack.queens.core.time.TimeProvider

class FakeTimeProvider(
    var currentTimeMillis: Long = 0L,
    var elapsedRealtimeMillis: Long = 0L,
) : TimeProvider {

    override fun currentTimeMillis(): Long = currentTimeMillis

    override fun elapsedRealtimeMillis(): Long = elapsedRealtimeMillis

    fun advanceBy(millis: Long) {
        currentTimeMillis += millis
        elapsedRealtimeMillis += millis
    }
}
