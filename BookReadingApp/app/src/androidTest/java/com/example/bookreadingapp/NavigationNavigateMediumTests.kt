package com.example.bookreadingapp

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onChildren
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import com.example.bookreadingapp.ui.NavRoutes
import com.example.bookreadingapp.ui.screens.ContentsScreen
import com.example.bookreadingapp.ui.screens.HomeScreen
import com.example.bookreadingapp.ui.screens.LibraryScreen
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import com.example.bookreadingapp.viewModels.ReadingAppViewModelFactory
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class NavigationNavigateMediumTests {
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
        val context = ApplicationProvider.getApplicationContext<Context>()
        val application = context.applicationContext as Application
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)

        composeTestRule.setContent {
            BookReadingAppTheme {
                val navController = rememberNavController()
                val viewModel: ReadingAppViewModel = viewModel(factory = ReadingAppViewModelFactory(context, application))
                NavHost(navController = navController, startDestination = NavRoutes.Home.route) {
                    composable(NavRoutes.Library.route) {
                        LibraryScreen(navController = navController, viewModel = viewModel)
                    }
                    composable(NavRoutes.Contents.route) {
                        ContentsScreen(
                            bookId = 1,
                            navController = navController,
                            viewModel = viewModel
                        )
                    }
                    composable(NavRoutes.Home.route) {
                        HomeScreen(navController)
                    }
                }
                BookReadingApp(windowSizeClass = WindowWidthSizeClass.Medium, modifier = Modifier, preferences = preferences)
            }
        }
    }


    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateNavBarNavigateLibrary() {
        composeTestRule.onNode(hasClickLabel("side nav rail")).onChildren()[1].performClick()
        composeTestRule.onNodeWithText("Read")
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateNavBarNavigateSearch() {
        composeTestRule.onNode(hasClickLabel("side nav rail")).onChildren()[2].performClick()
        composeTestRule.onNodeWithText("Search for books").assertExists()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateNavBarNavigateHome() {
        composeTestRule.onNode(hasClickLabel("side nav rail")).onChildren()[1].performClick()
        composeTestRule.onNode(hasClickLabel("side nav rail")).onChildren()[0].performClick()
    }
}