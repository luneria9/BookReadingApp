package com.example.bookreadingapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.bookreadingapp.ui.screens.SearchScreen
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