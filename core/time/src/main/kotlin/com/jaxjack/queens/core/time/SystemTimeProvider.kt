package com.jaxjack.queens.core.time

import android.os.SystemClock
import javax.inject.Inject

internal class SystemTimeProvider @Inject constructor() : TimeProvider {

    override fun currentTimeMillis(): Long = System.currentTimeMillis()

    override fun elapsedRealtimeMillis(): Long = SystemClock.elapsedRealtime()
}
