package com.example.bookreadingapp.ui

//referenced from https://gitlab.com/crdavis/adaptivenavigationegcode/-/tree/master?ref_type=heads
sealed class NavRoutes(val route: String) {
    object Home : NavRoutes("home")
    object Library : NavRoutes("library")
    object Search : NavRoutes("search")
    object Contents : NavRoutes("contents/{bookId}") {
        fun createRoute(bookId: Int) = "contents/$bookId"
    }
    object Reading : NavRoutes("reading")
}
