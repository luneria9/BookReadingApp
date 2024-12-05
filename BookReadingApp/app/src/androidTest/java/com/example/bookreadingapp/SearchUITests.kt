package com.example.bookreadingapp

import android.app.Application
import android.content.Context
import android.preference.PreferenceManager
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import androidx.test.core.app.ApplicationProvider
import com.example.bookreadingapp.ui.screens.SearchScreen
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import com.example.bookreadingapp.viewModels.ReadingAppViewModelFactory
import org.junit.Rule
import org.junit.Test


internal class SearchUITests {
    @get:Rule
    val composeTestRule = createComposeRule()

    private lateinit var context: Context
    private lateinit var viewModel: ReadingAppViewModel
    private lateinit var application: Application

    @OptIn(ExperimentalMaterial3Api::class)
    @Test
    fun validateSearchMessage() {
        context = ApplicationProvider.getApplicationContext()
        application = context.applicationContext as Application

        // Use the factory to get the viewModel
        val viewModelFactory = ReadingAppViewModelFactory(context, application)

        composeTestRule.setContent {
            BookReadingAppTheme {
                viewModel = viewModel(factory = viewModelFactory)
                SearchScreen(
                    viewModel = viewModel,
                    navController = rememberNavController()
                )
            }
        }

        composeTestRule.onNodeWithText("Search for books").assertExists()
    }
}