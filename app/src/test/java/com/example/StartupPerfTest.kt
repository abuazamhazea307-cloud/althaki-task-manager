package com.example

import androidx.test.core.app.ActivityScenario
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.shadows.ShadowLog
import org.robolectric.shadows.ShadowLooper
import java.util.concurrent.TimeUnit

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
        
        println("=== IDLING MAIN LOOPER FOR 2.5 SECONDS TO TRIGGER SPLASH -> HOME TRANSITION ===")
        ShadowLooper.idleMainLooper(2500, TimeUnit.MILLISECONDS)
        
        println("=== SCENARIO RUN COMPLETED ===")
        scenario.close()
    }
}
