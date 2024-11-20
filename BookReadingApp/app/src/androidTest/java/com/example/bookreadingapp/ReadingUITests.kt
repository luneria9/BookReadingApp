package com.example.bookreadingapp

import android.content.Context
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
import com.example.bookreadingapp.viewModels.ReadingAppViewModelFactory
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.mock

class ReadingUITests {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var viewModel: ReadingAppViewModel

    @Before
    fun setup() {
        context = mock(Context::class.java)

        // Use the factory to get the viewModel
        val viewModelFactory = ReadingAppViewModelFactory(context)
        composeTestRule.setContent {
            BookReadingAppTheme {
                viewModel = viewModel(factory = viewModelFactory)

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