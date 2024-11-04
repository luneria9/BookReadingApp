package com.example.bookreadingapp
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import com.example.bookreadingapp.ui.screens.HomeScreen
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import org.junit.Rule
import org.junit.Test

class HomeScreenTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun validateHomeScreenTitle() {
        composeTestRule.setContent {
            BookReadingAppTheme {
                val navController = rememberNavController()
                HomeScreen(navController = navController)
            }
        }

        composeTestRule.onNodeWithText("Welcome to Book Reader").assertExists()
    }
}




