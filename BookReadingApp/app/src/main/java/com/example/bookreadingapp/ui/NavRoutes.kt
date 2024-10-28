package com.example.bookreadingapp.ui

sealed class NavRoutes (val route: String) {
    object Home : NavRoutes("home")
    object Library : NavRoutes("library")
    object Search : NavRoutes("search")
    object Contents : NavRoutes("contents")
    object Reading : NavRoutes("reading")
}