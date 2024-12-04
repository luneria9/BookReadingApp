@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bookreadingapp

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Expanded
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Medium
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.bookreadingapp.ui.NavBarItems
import com.example.bookreadingapp.ui.NavRoutes.*
import com.example.bookreadingapp.ui.screens.*
import com.example.bookreadingapp.ui.utils.AdaptiveNavigationType
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass as calculateWindowSizeClass1
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import com.example.bookreadingapp.viewModels.ReadingAppViewModel
import com.example.bookreadingapp.viewModels.ReadingAppViewModelFactory
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {
    private val viewModel: ReadingAppViewModel by viewModels {
        ReadingAppViewModelFactory(this.applicationContext, application) // Use application context to prevent memory leaks
    }
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookReadingAppTheme  {
                val windowSize = calculateWindowSizeClass1(this)
                val sharedPref = this.getPreferences(Context.MODE_PRIVATE)

                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BookReadingApp(
                        windowSizeClass = windowSize.widthSizeClass,
                        viewModel = viewModel,
                        preferences = sharedPref,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

// Defines navigation routes for the app
@Composable
fun NavigationHost(
    navController: NavHostController,
    viewModel: ReadingAppViewModel,
    preferences: SharedPreferences,
) {
    var startRoute = Home.route
    var lastBook = preferences.getInt(stringResource(R.string.last_location_book), -1)
    var lastChapter = preferences.getInt(stringResource(R.string.last_location_chapter), -1)

    if (lastBook != -1 && lastChapter != -1)
        startRoute = Reading.route

    val isBookSelected by remember { mutableStateOf(false) }
    NavHost(navController = navController, startDestination = startRoute) {
        composable(Home.route) {
            HomeScreen(navController)
        }

        composable(Library.route) {
            LibraryScreen(navController, viewModel)
        }

        composable(Search.route) {
            SearchScreen(viewModel, navController, isBookSelected)
        }

        composable(
            route = Contents.route,
            arguments = listOf(navArgument("bookId") { type = NavType.IntType })
        ) { backStackEntry ->
            val bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
            ContentsScreen(
                bookId = bookId,
                navController = navController,
                viewModel = viewModel
            )
        }

        composable(
            route = Reading.route,
            arguments = listOf(
                navArgument("bookId") { type = NavType.IntType },
                navArgument("chapterId") { type = NavType.IntType }
            )
        ) { backStackEntry ->
            var bookId = backStackEntry.arguments?.getInt("bookId") ?: 0
            var chapterId = backStackEntry.arguments?.getInt("chapterId") ?: 0

            if (lastBook != -1 && lastChapter != -1) {
                bookId = lastBook
                chapterId = lastChapter
            }

            // Reset location
            lastBook = -1
            lastChapter = -1

            ReadingScreen(
                preferences = preferences,
                bookId = bookId,
                chapterId = chapterId,
                readingMode = viewModel.readingMode,
                onReadingCheck = { viewModel.toggleReadingMode() },
                viewModel = viewModel,
                navController = navController
            )
        }
    }
}

// Sets navigation type based on window size
@Composable
fun getAdaptiveNavigationType(windowSizeClass: WindowWidthSizeClass): AdaptiveNavigationType {
    return when (windowSizeClass) {
        Compact -> AdaptiveNavigationType.BOTTOM_NAVIGATION
        Medium -> AdaptiveNavigationType.NAVIGATION_RAIL
        else -> AdaptiveNavigationType.PERMANENT_NAVIGATION_DRAWER
    }
}

// Composable to adapts layout to screen size
@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
@Composable
@ExperimentalMaterial3Api
fun BookReadingApp(
    windowSizeClass: WindowWidthSizeClass,
    viewModel: ReadingAppViewModel = viewModel(),
    preferences: SharedPreferences,
    modifier: Modifier
) {
    val navController = rememberNavController()
    val adaptiveNavigationType = getAdaptiveNavigationType(windowSizeClass)
    BookReadingScaffold(navController, adaptiveNavigationType, viewModel, preferences)

    val booksToDownload = stringArrayResource(R.array.book_urls)
    val bookTitles = stringArrayResource(R.array.book_titles)
    val coroutineScope = rememberCoroutineScope()
    booksToDownload.forEachIndexed { index, url ->
        if (index < 3) {
            runBlocking {
                val fileName = url.substringAfterLast("/")
                viewModel.downloadUnzip(url, fileName, bookTitles[index])
            }
        }
    }
}

// Scaffold structure with conditional top and bottom bars
@Composable
fun BookReadingScaffold(
    navController: NavHostController,
    adaptiveNavigationType: AdaptiveNavigationType,
    viewModel: ReadingAppViewModel,
    preferences: SharedPreferences,
) {
    Scaffold(
        topBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute != Contents.route && currentRoute != Reading.route) {
                BookReadingTopAppBar()
            }
        },
        bottomBar = {
            if (adaptiveNavigationType == AdaptiveNavigationType.BOTTOM_NAVIGATION && !viewModel.readingMode) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        BookReadingContent(
            navController = navController,
            adaptiveNavigationType = adaptiveNavigationType,
            viewModel = viewModel,
            paddingValues = paddingValues,
            preferences = preferences
        )
    }
}

// Displays content with adaptive navigation layout
@Composable
fun BookReadingContent(
    navController: NavHostController,
    adaptiveNavigationType: AdaptiveNavigationType,
    viewModel: ReadingAppViewModel,
    paddingValues: PaddingValues,
    preferences: SharedPreferences,
) {
    Row(modifier = Modifier.padding(paddingValues)) {
        if (adaptiveNavigationType == AdaptiveNavigationType.PERMANENT_NAVIGATION_DRAWER) {
            PermanentNavigationDrawerComponent(viewModel, navController, preferences)
        } else {
            if (adaptiveNavigationType == AdaptiveNavigationType.NAVIGATION_RAIL && !viewModel.readingMode) {
                NavigationRailComponent(navController = navController)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavigationHost(
                    navController = navController,
                    viewModel = viewModel,
                    preferences = preferences
                )
            }
        }
    }
}

//referenced from https://gitlab.com/crdavis/adaptivenavigationegcode/-/tree/master?ref_type=heads
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar (
        Modifier.clickable(
            onClickLabel = stringResource(R.string.bottom_nav_bar),
            onClick = {}
        )
    ){
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoutes = backStackEntry?.destination?.route
        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                modifier = Modifier.testTag(stringResource(R.string.nav_bar_item)),
                selected = currentRoutes == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = navItem.image,
                        contentDescription = navItem.title
                    )
                },
                label = {
                    Text(text = navItem.title)
                },
            )
        }
    }
}

//referenced from https://gitlab.com/crdavis/adaptivenavigationegcode/-/tree/master?ref_type=heads
@Composable
fun NavigationRailComponent(navController: NavHostController) {
    NavigationRail (
        Modifier.clickable(
            onClickLabel = stringResource(R.string.side_nav_rail),
            onClick = {}
        )
    ){
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoutes = backStackEntry?.destination?.route
        NavBarItems.BarItems.forEach { navItem ->
            NavigationRailItem(
                selected = currentRoutes == navItem.route,
                onClick = { navController.navigate(navItem.route) },
                icon = {
                    Icon(navItem.image, contentDescription = navItem.title)
                },
                label = {
                    Text(text = navItem.title)
                }
            )
        }
    }
}

//referenced from https://gitlab.com/crdavis/adaptivenavigationegcode/-/tree/master?ref_type=heads
@Composable
fun DrawerContent(viewModel: ReadingAppViewModel, navController: NavHostController, currentRoutes: String?) {
    if (!viewModel.readingMode) {
        PermanentDrawerSheet(
            modifier = Modifier.testTag(stringResource(R.string.perm_nav)),
        ) {
            Column {
                Spacer(Modifier.height(dimensionResource(R.dimen.spacer_medium)))
                NavBarItems.BarItems.forEach { navItem ->
                    NavigationDrawerItem(
                        modifier = Modifier.testTag(stringResource(R.string.drawer_item)),
                        selected = currentRoutes == navItem.route,
                        onClick = {
                            navController.navigate(navItem.route)
                        },
                        icon = {
                            Icon(navItem.image, contentDescription = navItem.title)
                        },
                        label = { Text(text = navItem.title) }
                    )
                }
            }
        }
    }
}

//referenced from https://gitlab.com/crdavis/adaptivenavigationegcode/-/tree/master?ref_type=heads
@Composable
fun PermanentNavigationDrawerComponent(
    viewModel: ReadingAppViewModel,
    navController: NavHostController,
    preferences: SharedPreferences,
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoutes = backStackEntry?.destination?.route
    PermanentNavigationDrawer(
        drawerContent = {
            DrawerContent(viewModel = viewModel, navController = navController, currentRoutes = currentRoutes)
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                NavigationHost(
                    navController = navController,
                    viewModel = viewModel,
                    preferences = preferences
                )
            }
        }
    )
}

// Referenced to https://developer.android.com/codelabs/basic-android-kotlin-compose-material-theming#5
@Composable
fun BookReadingTopAppBar(modifier: Modifier = Modifier){
    CenterAlignedTopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    modifier = Modifier
                        .size(120.dp)
                        .padding(dimensionResource(id = R.dimen.padding_medium)),
                    painter = painterResource(R.drawable.logo),
                    contentDescription = null
                )
                Text(
                    text = stringResource(R.string.title),
                    style = MaterialTheme.typography.displayLarge
                )
            }
        },
        modifier = modifier
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 1000)
@Composable
fun GreetingPreview() {
    BookReadingAppTheme {
        BookReadingApp(windowSizeClass = Expanded, preferences = Activity().getPreferences(Context.MODE_PRIVATE), modifier = Modifier)
    }
}