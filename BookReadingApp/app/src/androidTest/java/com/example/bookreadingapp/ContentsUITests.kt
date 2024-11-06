package com.example.bookreadingapp

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.example.bookreadingapp.ui.screens.ContentsScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ContentsUITests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Before
    fun setup() {
        composeTestRule.setContent {
            ContentsScreen()
        }
    }

    @Test
    fun validateTitle() {
        composeTestRule.onNodeWithText("Table Of Contents").assertExists(
            "No node with this text was found."
        )
    }
}