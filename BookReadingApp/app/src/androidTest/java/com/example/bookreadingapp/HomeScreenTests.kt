package com.example.bookreadingapp
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.runner.RunWith
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookreadingapp.ui.NavRoutes
import com.example.bookreadingapp.ui.screens.HomeScreen
import com.example.bookreadingapp.ui.screens.LibraryScreen
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import com.example.bookreadingapp.viewModels.ReadingAppViewModelFactory
import org.junit.Before
import org.junit.Rule
import org.junit.Test

//Referenced to https://gitlab.com/crdavis/testingexamplecode/-/blob/master/app/src/androidTest/java/com/example/testingexamplecode/MainActivityKtTest.kt?ref_type=heads
//Run with JUnit test runner provided by Android Testing Framework
@RunWith(AndroidJUnit4::class)
class HomeScreenTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    //Set up the content before running each test
    @Before
    fun setUp() {
        composeTestRule.setContent {
            BookReadingAppTheme {
                val navController = rememberNavController()
                val context = LocalContext.current
                val viewModel: ReadingAppViewModel = viewModel(factory = ReadingAppViewModelFactory(context))
                NavHost(navController = navController, startDestination = NavRoutes.Home.route) {
                    composable(NavRoutes.Home.route) { HomeScreen(navController = navController) }
                    composable(NavRoutes.Library.route) { LibraryScreen(navController = navController, viewModel = viewModel) }
                }
            }
        }
    }

    // Verify welcome card is displayed
    @Test
    fun welcomeCardIsDisplayed() {
        composeTestRule.onNodeWithText("Welcome to Book Reader").assertExists().assertIsDisplayed()
        composeTestRule.onNodeWithText("Explore freely available textbooks with images and tables. Enjoy seamless navigation across different screen sizes!").assertExists().assertIsDisplayed()
    }

    // Test button navigation to Library screen
    @Test
    fun buttonNavigatesToLibrary() {
        composeTestRule.onNodeWithText("Go to Library").assertIsDisplayed()
        composeTestRule.onNodeWithText("Go to Library").performClick()
    }

    // Verify image is displayed
    @Test
    fun imageIsDisplayed() {
        composeTestRule.onNodeWithContentDescription("Home Screen Image").assertExists().assertIsDisplayed()
    }
}

