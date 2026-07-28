package com.example.debug

import android.os.SystemClock
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object StartupTracer {
    private const val TAG = "StartupTrace"
    private val appStartTime = SystemClock.elapsedRealtime()
    private val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS", Locale.US)

    // Tracks if we have already marked certain single-time events
    private val markedStages = mutableSetOf<String>()

    fun mark(stage: String) {
        synchronized(markedStages) {
            if (markedStages.contains(stage)) return
            markedStages.add(stage)
        }

        val elapsed = SystemClock.elapsedRealtime() - appStartTime
        val absoluteTime = sdf.format(Date())
        val threadName = Thread.currentThread().name
        Log.i(TAG, "[$stage] | Elapsed: ${elapsed}ms | Absolute: $absoluteTime | Thread: $threadName")
    }
}
