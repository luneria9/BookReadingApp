package com.example.bookreadingapp

import android.app.Application
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.navigation.NavController
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.example.bookreadingapp.fileSystem.FileSystem
import com.example.bookreadingapp.ui.screens.ContentsScreen
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

class ContentsUITests {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var navController: NavController
    private lateinit var viewModel: ReadingAppViewModel

    @Mock
    private lateinit var fileSystem: FileSystem

    @Before
    fun setup() {
        MockitoAnnotations.openMocks(this)

        val application = ApplicationProvider.getApplicationContext<Application>()
        navController = TestNavHostController(application)
        viewModel = ReadingAppViewModel(
            fileSystem = fileSystem,
            application = application
        )

        composeTestRule.setContent {
            ContentsScreen(
                bookId = 1,
                navController = navController,
                viewModel = viewModel
            )
        }
    }

    @Test
    fun validateTitle() {
        composeTestRule.onNodeWithText("Table Of Contents").assertExists(
            "No node with this text was found."
        )
    }
}