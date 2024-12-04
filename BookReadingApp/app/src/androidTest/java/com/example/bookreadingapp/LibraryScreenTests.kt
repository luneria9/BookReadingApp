//package com.example.bookreadingapp
//import androidx.compose.material3.ExperimentalMaterial3Api
//import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
//import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
//import androidx.compose.ui.test.assertCountEquals
//import androidx.compose.ui.test.junit4.createComposeRule
//import androidx.compose.ui.test.onAllNodesWithContentDescription
//import androidx.compose.ui.test.performClick
//import androidx.lifecycle.viewmodel.compose.viewModel
//import androidx.navigation.compose.NavHost
//import androidx.navigation.compose.composable
//import androidx.navigation.compose.rememberNavController
//import com.example.bookreadingapp.ui.NavRoutes
//import com.example.bookreadingapp.ui.screens.ContentsScreen
//import com.example.bookreadingapp.ui.screens.LibraryScreen
//import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
//import com.example.bookreadingapp.viewModels.ReadingAppViewModel
//import com.example.bookreadingapp.viewModels.ReadingAppViewModelFactory
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//
////Referenced to https://gitlab.com/crdavis/testingexamplecode/-/blob/master/app/src/androidTest/java/com/example/testingexamplecode/MainActivityKtTest.kt?ref_type=heads
//class LibraryScreenTests {
//    @get:Rule
//    val composeTestRule = createComposeRule()
//
//    // Set up the content before each test
//    @OptIn(ExperimentalMaterial3Api::class)
//    @Before
//    fun setUp() {
//        composeTestRule.setContent {
//            BookReadingAppTheme {
//                val navController = rememberNavController()
//                val context = LocalContext.current
//                val viewModel: ReadingAppViewModel = viewModel(factory = ReadingAppViewModelFactory(context))
//                NavHost(navController = navController, startDestination = NavRoutes.Library.route) {
//                    composable(NavRoutes.Library.route) {
//                        LibraryScreen(navController = navController, viewModel = viewModel)
//                    }
//                    composable(NavRoutes.Contents.route) {
//                        ContentsScreen()
//                    }
//                }
//                BookReadingApp(windowSizeClass = WindowWidthSizeClass.Expanded, modifier = Modifier)
//            }
//        }
//    }
//
//    // Test to ensure that the LibraryScreen displays 6 books
//    @Test
//    fun validateLibraryScreenDisplaysBooks() {
//        composeTestRule.onAllNodesWithContentDescription("Book Item").assertCountEquals(3)
//    }
//
//    // Test to verify that clicking on a book item navigates to the ContentsScreen
//    @Test
//    fun testNavigateToContentsScreen() {
//        composeTestRule.onAllNodesWithContentDescription("Book Item")[0].performClick()
//    }
//}
//
//
//
//
