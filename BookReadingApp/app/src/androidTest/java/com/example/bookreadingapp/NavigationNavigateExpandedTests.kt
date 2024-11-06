package com.example.bookreadingapp

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onChild
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.bookreadingapp.ui.NavRoutes
import com.example.bookreadingapp.ui.screens.ContentsScreen
import com.example.bookreadingapp.ui.screens.HomeScreen
import com.example.bookreadingapp.ui.screens.LibraryScreen
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationNavigateExpandedTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    // referenced from https://proandroiddev.com/stop-using-test-tags-in-the-jetpack-compose-production-code-b98e2679221f
    private fun hasClickLabel(label: String) = SemanticsMatcher("Clickable action with label: $label") {
        it.config.getOrNull(
            SemanticsActions.OnClick
        )?.label == label
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Before
    fun setUp() {
        composeTestRule.setContent {
            BookReadingAppTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = NavRoutes.Home.route) {
                    composable(NavRoutes.Library.route) {
                        LibraryScreen(navController)
                    }
                    composable(NavRoutes.Contents.route) {
                        ContentsScreen()
                    }
                    composable(NavRoutes.Home.route) {
                        HomeScreen(navController)
                    }
                }
                BookReadingApp(windowSizeClass = WindowWidthSizeClass.Expanded, modifier = Modifier)
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateNavBarNavigateLibrary() {
        composeTestRule.onAllNodesWithTag("drawer item")[1].performClick()
        composeTestRule.onNodeWithText("Read")
    }

    // multiple search for books?
    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateNavBarNavigateSearch() {
        composeTestRule.onAllNodesWithTag("drawer item")[2].performClick()
        composeTestRule.onNodeWithText("Search for books").assertExists()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateNavBarNavigateHome() {
        composeTestRule.onAllNodesWithTag("drawer item")[2].performClick()
        composeTestRule.onAllNodesWithTag("drawer item")[0].performClick()
    }
}