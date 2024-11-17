package com.example.bookreadingapp

import android.content.Context
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import com.example.bookreadingapp.fileSystem.FileSystem
import com.example.bookreadingapp.ui.NavBarItems
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import org.junit.Rule
import org.junit.Test

internal class NavigationTests {
    @get:Rule
    val composeTestRule = createComposeRule()

    // referenced from https://proandroiddev.com/stop-using-test-tags-in-the-jetpack-compose-production-code-b98e2679221f
    private fun hasClickLabel(label: String) = SemanticsMatcher("Clickable action with label: $label") {
        it.config.getOrNull(
            SemanticsActions.OnClick
        )?.label == label
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateNavBar() {
        composeTestRule.setContent {
            BookReadingApp(windowSizeClass = WindowWidthSizeClass.Compact, modifier = Modifier)
        }

        composeTestRule.onNode(hasClickLabel("bottom nav bar")).assertExists()
        composeTestRule.onNode(hasClickLabel("side nav rail")).assertDoesNotExist()
        composeTestRule.onNodeWithTag("perm nav").assertDoesNotExist()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateNavRail() {
        composeTestRule.setContent {
            BookReadingApp(windowSizeClass = WindowWidthSizeClass.Medium, modifier = Modifier)
        }

        composeTestRule.onNode(hasClickLabel("bottom nav bar")).assertDoesNotExist()
        composeTestRule.onNode(hasClickLabel("side nav rail")).assertExists()
        composeTestRule.onNodeWithTag("perm nav").assertDoesNotExist()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateNavDrawer() {
        composeTestRule.setContent {
            BookReadingApp(windowSizeClass = WindowWidthSizeClass.Expanded, modifier = Modifier)
        }

        composeTestRule.onNode(hasClickLabel("bottom nav bar")).assertDoesNotExist()
        composeTestRule.onNode(hasClickLabel("side nav rail")).assertDoesNotExist()
        composeTestRule.onNodeWithTag("perm nav").assertExists()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun bottomNavigationBar_isVisibleInCompactMode() {
        // Set up the composable with Compact mode to trigger the bottom navigation bar
        composeTestRule.setContent {
            BookReadingAppTheme {
                BookReadingApp(
                    windowSizeClass = WindowWidthSizeClass.Compact,
                    modifier = Modifier
                )
            }
        }

        // Assert that the bottom navigation bar is displayed
        composeTestRule.onNode(hasClickLabel("bottom nav bar")).assertExists()
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun bottomNavigationBar_displaysAllNavItems() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            BottomNavigationBar(navController = navController)
        }

        // Assert that all navigation items are present in the bottom navigation bar
        NavBarItems.BarItems.forEach { navItem ->
            composeTestRule.onNodeWithText(navItem.title).assertExists()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun bottomNavigationBar_isHiddenInReadingMode() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val fileSystem = FileSystem(context)

        composeTestRule.setContent {
            val viewModel = ReadingAppViewModel(fileSystem).apply { toggleReadingMode() }
            BookReadingApp(
                windowSizeClass = WindowWidthSizeClass.Compact,
                viewModel = viewModel,
                modifier = Modifier
            )
        }

        // Assert that the bottom navigation bar is hidden in reading mode
        composeTestRule.onNode(hasClickLabel("bottom nav bar")).assertDoesNotExist()
    }
}