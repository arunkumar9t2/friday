package dev.arunkumar.jarvis.ui.screens.tasks

import android.graphics.Bitmap
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.platform.app.InstrumentationRegistry
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dev.arunkumar.jarvis.MainActivity
import dev.arunkumar.jarvis.data.ticktick.SyncResult
import dev.arunkumar.jarvis.data.ticktick.TickTickSyncManager
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import java.io.File
import javax.inject.Inject

@HiltAndroidTest
class TickTickTaskListTest {

  @get:Rule(order = 0)
  val hiltRule = HiltAndroidRule(this)

  @get:Rule(order = 1)
  val composeTestRule = createAndroidComposeRule<MainActivity>()

  @Inject
  lateinit var syncManager: TickTickSyncManager

  @Before
  fun setup() {
    hiltRule.inject()
  }

  @Test
  fun taskListLoadsFromTickTickProxy() {
    // Step 1: Sync data from live TickTick proxy API
    val syncResult = runBlocking { syncManager.sync() }
    assertTrue(
      "Sync failed — check network/API key. Result: $syncResult",
      syncResult is SyncResult.Success
    )

    // Step 2: Navigate to Tasks screen (click "Tasks" button on HomeScreen)
    composeTestRule.onNodeWithText("Tasks").performClick()

    // Step 3: Wait for task list to appear (Room → Presenter → UI)
    composeTestRule.waitUntil(timeoutMillis = 15_000) {
      composeTestRule
        .onAllNodesWithTag("tasks_list")
        .fetchSemanticsNodes()
        .isNotEmpty()
    }

    // Step 4: Assert task list is visible and has task cards
    composeTestRule.onNodeWithTag("tasks_list").assertIsDisplayed()
    val hasTaskCardTag = SemanticsMatcher("has task_card_ prefix") {
      it.config.contains(SemanticsProperties.TestTag) &&
        it.config[SemanticsProperties.TestTag].startsWith("task_card_")
    }
    composeTestRule.waitUntil(timeoutMillis = 5_000) {
      composeTestRule
        .onAllNodes(hasTaskCardTag, useUnmergedTree = true)
        .fetchSemanticsNodes()
        .isNotEmpty()
    }

    // Step 5: Capture screenshot for manual verification
    saveScreenshot("task_list_loaded")
  }

  private fun saveScreenshot(name: String) {
    val bitmap = composeTestRule.onRoot().captureToImage().asAndroidBitmap()
    val dir = File(
      InstrumentationRegistry.getInstrumentation().targetContext.filesDir,
      "test-screenshots"
    )
    dir.mkdirs()
    File(dir, "$name.png").outputStream().use { out ->
      bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
    }
  }
}
