@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bookreadingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Compact
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Expanded
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass.Companion.Medium
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavHostController
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.bookreadingapp.ui.NavBarItems
import com.example.bookreadingapp.ui.NavRoutes.*
import com.example.bookreadingapp.ui.screens.*
import com.example.bookreadingapp.ui.utils.AdaptiveNavigationType
import androidx.compose.material3.windowsizeclass.calculateWindowSizeClass as calculateWindowSizeClass1
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import com.example.bookreadingapp.ui.NavRoutes
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme
import com.example.bookreadingapp.viewModels.ReadingAppViewModel


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookReadingAppTheme  {
                val windowSize = calculateWindowSizeClass1(this)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    BookReadingApp(
                        windowSizeClass = windowSize.widthSizeClass,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationHost(navController: NavHostController, viewModel: ReadingAppViewModel) {
    NavHost(navController = navController, startDestination = NavRoutes.Home.route
    ) {
        composable(Home.route) {
            HomeScreen(navController)
        }

        composable(Library.route) {
            LibraryScreen(navController)
        }

        composable(Search.route) {
            SearchScreen()
        }

        composable(Contents.route) {
            ContentsScreen()
        }

        composable(Reading.route) {
            ReadingScreen(
                readingMode = viewModel.readingMode,
                onReadingCheck = { viewModel.toggleReadingMode() }
            )
        }
    }
}

@Composable
@ExperimentalMaterial3Api
fun BookReadingApp(
    windowSizeClass: WindowWidthSizeClass,
    viewModel: ReadingAppViewModel = viewModel(),
    modifier: Modifier
) {
    val navController = rememberNavController()

     val adaptiveNavigationType = when (windowSizeClass) {
        Compact -> AdaptiveNavigationType.BOTTOM_NAVIGATION
        Medium -> AdaptiveNavigationType.NAVIGATION_RAIL
        else -> AdaptiveNavigationType.PERMANENT_NAVIGATION_DRAWER
    }

    Scaffold(
        topBar = {
            val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route
            if (currentRoute !== Contents.route || currentRoute !== Reading.route) {
                BookReadingTopAppBar()
            }
        },
        bottomBar = {
            if (adaptiveNavigationType == AdaptiveNavigationType.BOTTOM_NAVIGATION && !viewModel.readingMode) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        Row(modifier = Modifier.padding(paddingValues)) {
            if (adaptiveNavigationType == AdaptiveNavigationType.PERMANENT_NAVIGATION_DRAWER) {
                PermanentNavigationDrawerComponent(viewModel, navController)
            }
            if (adaptiveNavigationType == AdaptiveNavigationType.NAVIGATION_RAIL && !viewModel.readingMode) {
                NavigationRailComponent(navController = navController)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavigationHost(navController = navController, viewModel = viewModel)
            }
        }
    }
}

//referenced from https://gitlab.com/crdavis/adaptivenavigationegcode/-/tree/master?ref_type=heads
@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoutes = backStackEntry?.destination?.route
        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoutes == navItem.route,
                onClick = {
                    navController.navigate(navItem.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
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
    NavigationRail {
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
fun PermanentNavigationDrawerComponent(
    viewModel: ReadingAppViewModel,
    navController: NavHostController
) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoutes = backStackEntry?.destination?.route
    PermanentNavigationDrawer(
        drawerContent = {
            if (!viewModel.readingMode) {
                PermanentDrawerSheet {
                    Column {
                        Spacer(Modifier.height(dimensionResource(R.dimen.spacer_medium)))
                        NavBarItems.BarItems.forEach { navItem ->
                            NavigationDrawerItem(
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
        },
        content = {
            Box(modifier = Modifier.fillMaxSize()) {
                //The call to NavigationHost is necessary to display the screen based on the route
                NavigationHost(navController = navController, viewModel = viewModel)
            }
        }
    )
}

//Got code from code lab https://developer.android.com/codelabs/basic-android-kotlin-compose-material-theming#5
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
        BookReadingApp(windowSizeClass = Expanded, modifier = Modifier)
    }
}