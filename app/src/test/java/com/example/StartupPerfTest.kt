package com.example

import androidx.test.core.app.ActivityScenario
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class StartupPerfTest {

    @Test
    fun runStartupAndPrintLogs() {
        ShadowLog.stream = System.out
        println("=== STARTING ACTIVITY SCENARIO ===")
        val scenario = ActivityScenario.launch(MainActivity::class.java)
        scenario.onActivity { activity ->
            println("=== MainActivity launched successfully! ===")
        }
        scenario.close()
    }
}
