package com.example.bookreadingapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.example.bookreadingapp.ui.screens.SearchScreen
import org.junit.Rule
import org.junit.Test

internal class NavigationTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    // referenced from https://proandroiddev.com/stop-using-test-tags-in-the-jetpack-compose-production-code-b98e2679221f
    private fun hasClickLabel(label: String) = SemanticsMatcher("Clickable action with label: $label") {
        it.config.getOrNull(
            SemanticsActions.OnClick
        )?.label == label
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateNavBar() {
        composeTestRule.setContent {
            BookReadingApp(windowSizeClass = WindowWidthSizeClass.Compact, modifier = Modifier)
        }

        composeTestRule.onNode(hasClickLabel("bottom nav bar")).assertExists()
        composeTestRule.onNode(hasClickLabel("side nav rail")).assertDoesNotExist()
        composeTestRule.onNodeWithTag("perm nav").assertDoesNotExist()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateNavRail() {
        composeTestRule.setContent {
            BookReadingApp(windowSizeClass = WindowWidthSizeClass.Medium, modifier = Modifier)
        }

        composeTestRule.onNode(hasClickLabel("bottom nav bar")).assertDoesNotExist()
        composeTestRule.onNode(hasClickLabel("side nav rail")).assertExists()
        composeTestRule.onNodeWithTag("perm nav").assertDoesNotExist()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateNavDrawer() {
        composeTestRule.setContent {
            BookReadingApp(windowSizeClass = WindowWidthSizeClass.Expanded, modifier = Modifier)
        }

        composeTestRule.onNode(hasClickLabel("bottom nav bar")).assertDoesNotExist()
        composeTestRule.onNode(hasClickLabel("side nav rail")).assertDoesNotExist()
        composeTestRule.onNodeWithTag("perm nav").assertExists()
    }
}