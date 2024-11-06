package com.example.bookreadingapp
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onNodeWithText
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

//Referenced to https://gitlab.com/crdavis/testingexamplecode/-/blob/master/app/src/androidTest/java/com/example/testingexamplecode/MainActivityKtTest.kt?ref_type=heads
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
                    composable(NavRoutes.Contents.route) {
                        ContentsScreen()
                    }
                }
            }
        }
    }

    // Test to ensure that the LibraryScreen displays 6 books
    @Test
    fun validateLibraryScreenDisplaysBooks() {
        composeTestRule.onAllNodesWithContentDescription("Book Item").assertCountEquals(6)
    }

    // Test to verify that clicking on a book item navigates to the ContentsScreen
    @Test
    fun testNavigateToContentsScreen() {
        composeTestRule.onAllNodesWithContentDescription("Book Item")[0].performClick()
    }
}




