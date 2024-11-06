package com.example.bookreadingapp
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookreadingapp.ui.NavRoutes
import com.example.bookreadingapp.ui.screens.ContentsScreen
import com.example.bookreadingapp.ui.screens.LibraryScreen
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class LibraryScreenTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    // Set up the content before each test
    @Before
    fun setUp() {
        composeTestRule.setContent {
            BookReadingAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = NavRoutes.Library.route) {
                    composable(NavRoutes.Library.route) {
                        LibraryScreen(navController)
                    }
                    // You should add the Contents route here as well if you are testing navigation
                    composable(NavRoutes.Contents.route) {
                        ContentsScreen()
                    }
                }
            }
        }
    }

    @Test
    fun validateLibraryScreenDisplaysBooks() {
        composeTestRule.onAllNodesWithContentDescription("Book Item").assertCountEquals(6)
    }

    @Test
    fun testNavigateToContentsScreen() {
        composeTestRule.onAllNodesWithContentDescription("Book Item")[0].performClick()
    }
}




