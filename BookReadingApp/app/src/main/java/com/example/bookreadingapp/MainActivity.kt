@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.bookreadingapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import com.example.bookreadingapp.ui.theme.BookReadingAppTheme


class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            BookReadingAppTheme  {
                val windowSize = calculateWindowSizeClass1(this)
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    AdaptiveNavigationApp(
                        windowSizeClass = windowSize.widthSizeClass,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}

@Composable
fun NavigationHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Home.route
    ) {
        composable(Home.route) {
            HomeScreen()
        }

        composable(Library.route) {
            LibraryScreen()
        }

        composable(Search.route) {
            SearchScreen()
        }

        composable(Contents.route) {
            ContentsScreen()
        }

        composable(Reading.route) {
            ReadingScreen()
        }
    }
}

@Composable
@ExperimentalMaterial3Api
fun AdaptiveNavigationApp(windowSizeClass: WindowWidthSizeClass, modifier: Modifier) {
    val navController = rememberNavController()

     val adaptiveNavigationType = when (windowSizeClass) {
        Compact -> AdaptiveNavigationType.BOTTOM_NAVIGATION
        Medium -> AdaptiveNavigationType.NAVIGATION_RAIL
        else -> AdaptiveNavigationType.PERMANENT_NAVIGATION_DRAWER
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Adaptive Navigation illustration app") }) },
        bottomBar = {
            if (adaptiveNavigationType == AdaptiveNavigationType.BOTTOM_NAVIGATION) {
                BottomNavigationBar(navController = navController)
            }
        }
    ) { paddingValues ->
        Row(modifier = Modifier.padding(paddingValues)) {
            if (adaptiveNavigationType == AdaptiveNavigationType.PERMANENT_NAVIGATION_DRAWER) {
                PermanentNavigationDrawerComponent()
            }
            if (adaptiveNavigationType == AdaptiveNavigationType.NAVIGATION_RAIL) {
                NavigationRailComponent(navController = navController)
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                NavigationHost(navController = navController)
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    NavigationBar {
        val backStackEntry by navController.currentBackStackEntryAsState()
        val currentRoutes = backStackEntry?.destination?.route
        NavBarItems.BarItems.forEach { navItem ->
            NavigationBarItem(
                selected = currentRoutes == navItem.route,
                onClick = {
                    navController.navigate(navItem.route)
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

@Composable
fun NavigationRailComponent(navController: NavController) {
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

@Composable
fun PermanentNavigationDrawerComponent() {
     val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoutes = backStackEntry?.destination?.route
    PermanentNavigationDrawer(
        drawerContent = {
            PermanentDrawerSheet {
                Column {
                    Spacer(Modifier.height(12.dp))
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
            } },
        content = {
             Box(modifier = Modifier.fillMaxSize()) {
                 //The call to NavigationHost is necessary to display the screen based on the route
                NavigationHost(navController = navController)
            }
        }
    )
}



@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, widthDp = 1000)
@Composable
fun GreetingPreview() {
    BookReadingAppTheme {
        AdaptiveNavigationApp(windowSizeClass = Expanded, modifier = Modifier)
    }
}