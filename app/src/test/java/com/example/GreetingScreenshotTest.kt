package com.example

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onNodeWithTag
import androidx.navigation.compose.rememberNavController
import com.example.features.home.HomeScreen
import com.example.ui.theme.MyApplicationTheme
import com.github.takahirom.roborazzi.RobolectricDeviceQualifiers
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(qualifiers = RobolectricDeviceQualifiers.Pixel8, sdk = [36])
class GreetingScreenshotTest {

  @get:Rule val composeTestRule = createComposeRule()

  @Test
  fun greeting_screenshot() {
    composeTestRule.setContent {
      MyApplicationTheme {
        val navController = rememberNavController()
        HomeScreen(navController = navController)
      }
    }

    composeTestRule.onRoot().captureRoboImage(filePath = "src/test/screenshots/greeting.png")
  }

  @Test
  fun testSplashToHomeNavigation() {
    composeTestRule.setContent {
      MyApplicationTheme {
        val navController = rememberNavController()
        com.example.navigation.NavGraph(navController = navController)
      }
    }

    // Verify we start at Splash Screen (with our custom geometric diamond canvas)
    composeTestRule.onNodeWithTag("diamond_geometric_canvas").assertExists()

    // Advance virtual clock by 1 second
    composeTestRule.mainClock.advanceTimeBy(1000)

    // Verify we are still on Splash Screen as transition delay is 2000ms
    composeTestRule.onNodeWithTag("diamond_geometric_canvas").assertExists()

    // Advance virtual clock by another 1.1 second (total 2100ms)
    composeTestRule.mainClock.advanceTimeBy(1100)

    // Verify we are now on the Home Screen
    composeTestRule.onNodeWithTag("home_screen_root").assertExists()
  }
}
