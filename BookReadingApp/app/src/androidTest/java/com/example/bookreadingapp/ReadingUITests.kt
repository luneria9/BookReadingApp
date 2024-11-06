package com.example.bookreadingapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsOff
import androidx.compose.ui.test.assertIsOn
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.bookreadingapp.ui.screens.ReadingScreen
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReadingUITests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        composeTestRule.setContent {
            BookReadingAppTheme {
                val viewModel: ReadingAppViewModel = viewModel()

                ReadingScreen(
                    readingMode = viewModel.readingMode,
                    onReadingCheck = { viewModel.toggleReadingMode() }
                )
            }
        }
    }

    @ExperimentalMaterial3Api
    @Test
    fun readingModeSwitchTest() {
        // Tests if the switch correctly gets pressed.
        composeTestRule.onNodeWithTag("readingModeSwitch")
            .assertIsDisplayed()
            .assertIsOff()

        composeTestRule.onNodeWithTag("readingModeSwitch").performClick()

        composeTestRule.onNodeWithTag("readingModeSwitch").assertIsOn()
    }
}