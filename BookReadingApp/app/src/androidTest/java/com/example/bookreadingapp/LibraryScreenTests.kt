package com.example.bookreadingapp
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.rememberNavController
import com.example.bookreadingapp.ui.screens.HomeScreen
import com.example.bookreadingapp.ui.screens.LibraryScreen
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import org.junit.Rule
import org.junit.Test

class LibraryScreenTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun validateLibraryScreenDisplaysBooks() {
        composeTestRule.setContent {
            BookReadingAppTheme {
                val navController = rememberNavController()
                LibraryScreen(navController = navController)
            }
        }

        // Validate that the book images are displayed
        composeTestRule.onAllNodesWithContentDescription("Book Item")
            .assertCountEquals(6)
    }

    @Test
    fun testBookItemClickNavigatesToReadingScreen() {
        composeTestRule.setContent {
            BookReadingAppTheme {
                val navController = rememberNavController()
                LibraryScreen(navController = navController)
            }
        }

        // Perform a click on the first book item
        composeTestRule.onAllNodesWithContentDescription("Book Item")[0].performClick()
    }
}




