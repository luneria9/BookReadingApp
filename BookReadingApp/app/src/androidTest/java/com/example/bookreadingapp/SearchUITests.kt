package com.example.bookreadingapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.bookreadingapp.ui.screens.SearchScreen
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import org.junit.Rule
import org.junit.Test


internal class SearchUITests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateSearchMessage() {
        composeTestRule.setContent {
            SearchScreen()
        }

        composeTestRule.onNodeWithText("Search for books").assertExists()
    }
}